# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc

.ONESHELL:
.SUFFIXES:

# Artifact names:
export ORG = Scaled Markets
export PRODUCT_NAME = Dabl
export JAR_NAME = dabl
export TASK_JAR_NAME = taskruntime

# Output artifact names:
export package := scaledmarkets/dabl
export task_parser_package := $(package)/task
export test_package=scaledmarkets/dabl/test
export package_name = scaledmarkets.dabl
export task_package_name = scaledmarkets.dabl.task
export test_package_name = scaledmarkets.dabl.test
export main_class := $(package_name).Main
export task_main_class := $(task_package_name).Main

# Command aliases:
export SHELL = /bin/sh
export JAVAC = javac -Xlint:deprecation
export JAVA = java
export JAR = jar
export JAVADOC = javadoc

# Relative locations:
export CurDir := $(shell pwd)
export src_dir := $(CurDir)/java
export parser_build_dir := $(build_dir)/parser
export client_build_dir := $(build_dir)/client
export test_src_dir := $(CurDir)/test
export test_build_dir := $(CurDir)/buildtest
export test_package = $(package)/test
export testsourcefiles := $(test_src_dir)/$(test_package)/*.java
export testclassfiles := $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/exec/*.class
export sable_dabl_out_dir := $(CurDir)/SableCCOutput
export sable_task_out_dir := $(CurDir)/SableCCTaskOutput
export javadoc_dir := $(CurDir)/docs

# Java classpaths:
export buildcp := $(parser_build_dir):$(client_build_dir)
export compile_tests_cp := $(CUCUMBER_CLASSPATH):$(buildcp)
export test_cp := $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar
export third_party_cp := $(jaxrs):$(junixsocket):$(apache_http):$(jersey):$(javaxjson)

################################################################################
# Tasks


.PHONY: all manifest config gen_parser compile_parser parser jar compile dist \
	task_manifest task_jar image \
	check compile_tests test runsonar javadoc clean_parser clean_task_parser info

all: clean parser compile jar task_jar image compile_tests test

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

# ------------------------------------------------------------------------------
# Create parser.

parser: config
	sable_out_dir=$(sable_dabl_out_dir) \
		package=$(package) \
		parser_build_dir=$(parser_build_dir) \
		grammar_file=dabl.sablecc \
		make -f make_parser.makefile all

clean_parser:
	make -f make_parser.makefile clean


# ------------------------------------------------------------------------------
# Build compilers.


# Create the directory that will contain the compiled class files.
$(build_dir):
	mkdir $(build_dir)

# Create the directory that will contain the jar files that are created.
$(jar_dir):
	mkdir -p $(jar_dir)

client: $(jar_dir)
	make -f build_client.makefile all

clean_client:
	make -f build_client.makefile clean

task_runtime: $(jar_dir)
	make -f build_task_runtime.makefile all



# ------------------------------------------------------------------------------
# Tests


# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	echo "\n         namespace simple import abc      \n" > simple.dabl
	$(JAVA) -classpath $(buildcp) scaledmarkets.dabl.Main -t simple.dabl

# Compile the test source files.
compile_tests: $(test_build_dir)
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(compile_tests_cp) -d $(test_build_dir) \
		$(test_src_dir)/steps/$(test_package)/*.java \
		$(test_src_dir)/steps/$(test_package)/analyzer/*.java \
		$(test_src_dir)/steps/$(test_package)/docker/*.java \
		$(test_src_dir)/steps/$(test_package)/exec/*.java \
		$(test_src_dir)/steps/$(test_package)/task/*.java

# Run Cucumber tests.
# Note: We could export LD_LIBRARY_PATH instead of passing it in the java command.
test:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(third_party_cp):$(jar_dir)/$(JAR_NAME).jar \
		-Djava.library.path=$(junixsocketnative) \
		cucumber.api.cli.Main \
		--glue scaledmarkets.dabl.test \
		$(test_src_dir)/features \
		--tags @task
		#--tags @inputsandoutputs
		#--tags @patternsets,@pushlocalrepo

test_exec:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar \
		cucumber.api.cli.Main \
		--glue scaledmarkets.dabl.test \
		--tags @patternsets,@pushlocalrepo \
		$(test_src_dir)/features

test_check:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar \
		cucumber.api.cli.Main \
		$(test_src_dir)/features \

test_clean:
	rm -r -f $(test_build_dir)/*

# Perform code quality scans.
runsonar:
	$(SONAR_RUNNER)

cukehelp:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --help

cukever:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --version

clean: compile_clean test_clean clean_parser clean_task_parser
	rm -f Manifest
	rm -r -f $(javadoc_dir)/*

info:
	@echo "Makefile for $(PRODUCT_NAME)"
