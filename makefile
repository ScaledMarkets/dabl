# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc

.ONESHELL:
.SUFFIXES:

# Artifact names:
ORG = Scaled Markets
PRODUCT_NAME = Dabl
JAR_NAME = dabl
TASK_JAR_NAME = taskruntime

# Output artifact names:
package := scaledmarkets/dabl
task_parser_package := $(package)/task
test_package=scaledmarkets/dabl/test
package_name = scaledmarkets.dabl
task_package_name = scaledmarkets.dabl.task
test_package_name = scaledmarkets.dabl.test
main_class := $(package_name).Main
task_main_class := $(task_package_name).Main

# Intermediate artifacts:
classfiles := \
	$(build_dir)/$(package)/*.class \
	$(build_dir)/$(package)/docker/*.class \
	$(build_dir)/$(package)/exec/*.class \
	$(build_dir)/$(package)/helper/*.class \
	$(build_dir)/$(package)/analysis/*.class \
	$(build_dir)/$(package)/analyzer/*.class \
	$(build_dir)/$(package)/util/*.class \
	$(build_dir)/$(package)/lexer/*.class \
	$(build_dir)/$(package)/node/*.class \
	$(build_dir)/$(package)/repos/*.class \
	$(build_dir)/$(package)/parser/*.class

task_classfiles := \
	$(build_dir)/$(package)/task/*.class

# Command aliases:
SHELL = /bin/sh
JAVAC = javac -Xlint:deprecation
JAVA = java
JAR = jar
JAVADOC = javadoc

# Relative locations:
CurDir := $(shell pwd)
src_dir := $(CurDir)/java
test_src_dir := $(CurDir)/test
test_build_dir := $(CurDir)/buildtest
test_package = $(package)/test
testsourcefiles := $(test_src_dir)/$(test_package)/*.java
testclassfiles := $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/exec/*.class
sable_dabl_out_dir := $(CurDir)/SableCCOutput
sable_task_out_dir := $(CurDir)/SableCCTaskOutput
javadoc_dir := $(CurDir)/docs

# Java classpaths:
buildcp := $(build_dir)
compile_tests_cp := $(CUCUMBER_CLASSPATH):$(buildcp)
test_cp := $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar
third_party_cp := $(jaxrs):$(junixsocket):$(apache_http):$(jersey):$(javaxjson)

################################################################################
# Tasks


.PHONY: all manifest config gen_dabl_parser compile_dabl_parser parser jar compile dist \
	task_manifest task_jar image \
	check compile_tests test runsonar javadoc clean_dabl_parser clean_task_parser info

all: clean dabl_parser compile jar task_jar image compile_tests test

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

# ------------------------------------------------------------------------------
# Create parsers.

dabl_parser: config
	sable_out_dir=$(sable_dabl_out_dir) && \
		package=$(package) && \
		grammar_file=dabl.sablecc && \
		make -f make_parser.makefile all

task_parser:
	sable_out_dir=$(sable_task_out_dir) && \
		package=$(task_parser_package) && \
		grammar_file=task.sablecc && \
		make -f make_parser.makefile all

# ------------------------------------------------------------------------------
# Build compilers.


# Create the directory that will contain the compiled class files.
$(build_dir):
	mkdir $(build_dir)

client:
	make -f build_client.makefile all

task_runtime:
	make -f build_task_runtime.makefile all



# ------------------------------------------------------------------------------
# Tests


# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	echo "\n         namespace simple import abc      \n" > simple.dabl
	$(JAVA) -classpath $(build_dir) scaledmarkets.dabl.Main -t simple.dabl

# Compile the test source files.
compile_tests: $(test_build_dir)
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(compile_tests_cp) -d $(test_build_dir) \
		$(test_src_dir)/steps/$(test_package)/*.java \
		$(test_src_dir)/steps/$(test_package)/analyzer/*.java \
		$(test_src_dir)/steps/$(test_package)/docker/*.java \
		$(test_src_dir)/steps/$(test_package)/exec/*.java \
		$(src_dir)/sablecc/*.java

# Run Cucumber tests.
# Note: We could export LD_LIBRARY_PATH instead of passing it in the java command.
test:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(third_party_cp):$(jar_dir)/$(JAR_NAME).jar \
		-Djava.library.path=$(junixsocketnative) \
		cucumber.api.cli.Main \
		--glue scaledmarkets.dabl.test \
		$(test_src_dir)/features \
		--tags @patternsets,@pushlocalrepo

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

cukehelp:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --help

clean: compile_clean test_clean clean_dabl_parser clean_task_parser
	rm -f Manifest
	rm -r -f $(javadoc_dir)/*

info:
	@echo "Makefile for $(PRODUCT_NAME)"
