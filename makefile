# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc

.ONESHELL:
.SUFFIXES:

# Artifact names:
export ORG = Scaled Markets
export GroupId = scaledmarkets
export MavenBaseArtifactId = dabl
export PRODUCT_NAME = Dabl
export Description = "Dependent Artifact Build Language"
export COMMON_JAR_NAME = common
export PARSER_JAR_NAME = parser
export CLIENT_JAR_NAME = client
export TASK_JAR_NAME = taskruntime

# Output artifact names:
export package := scaledmarkets/dabl
export task_parser_package := $(package)/task
export test_package=scaledmarkets/dabl/test
export package_name = scaledmarkets.dabl
export client_package_name = $(package_name).client
export task_package_name = scaledmarkets.dabl.task
export test_package_name = scaledmarkets.dabl.test
export client_main_class := $(client_package_name).Main
export task_main_class := $(task_package_name).TaskExecutor

# Command aliases:
export SHELL = /bin/sh
export JAVAC = javac -Xlint:deprecation
export JAVA = java
export JAR = jar
export JAVADOC = javadoc

# Relative locations:
export ThisDir := $(shell pwd)
#export src_dir := $(ThisDir)/java
export sable_dabl_out_dir := $(ThisDir)/parser/java
export common_src_dir := $(ThisDir)/common/java
export client_src_dir := $(ThisDir)/client/java
export task_runtime_src_dir := $(ThisDir)/task_runtime/java
#export parser_build_dir := $(build_dir)/parser
#export common_build_dir := $(build_dir)/common
#export client_build_dir := $(build_dir)/client
#export task_runtime_build_dir := $(build_dir)/task_runtime
export test_src_dir := $(ThisDir)/test
export test_build_dir := $(ThisDir)/buildtest
export test_package = $(package)/test
export testsourcefiles := $(test_src_dir)/$(test_package)/*.java
export testclassfiles := $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/exec/*.class
export sable_task_out_dir := $(ThisDir)/SableCCTaskOutput
export javadoc_dir := $(ThisDir)/docs

# Aliases:
test := java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(third_party_cp):$(jar_dir)/$(JAR_NAME).jar
test := $(test) -Djava.library.path=$(junixsocketnative)
test := $(test) cucumber.api.cli.Main --glue scaledmarkets.dabl.test
test := $(test) $(test_src_dir)/features


################################################################################
# Tasks


.PHONY: all manifest config gen_parser compile_parser parser jar compile dist \
	task_manifest task_jar image \
	check compile_tests test runsonar javadoc clean_parser clean_task_parser info \
	common

all: clean parser compile jar task_jar task_runtime image compile_tests test mvnversion

mvnversion:
	$(MVN) --version

# ------------------------------------------------------------------------------
# Build client and task runtime.


# Create the directories that will contain the compiled class files.
$(build_dir):
	mkdir -p $(build_dir)

# Create the directory that will contain the parser source files.
$(sable_dabl_out_dir):
	mkdir -p $(sable_dabl_out_dir)/$(package)

$(parser_build_dir):
	mkdir -p $(parser_build_dir)

$(common_build_dir):
	mkdir -p $(common_build_dir)

$(client_build_dir):
	mkdir -p $(client_build_dir)

$(task_runtime_build_dir):
	mkdir -p $(task_runtime_build_dir)

# Create the directory that will contain the jar files that are created.
$(jar_dir):
	mkdir -p $(jar_dir)

# Install junixsocket.
install_junixsocket:
	$(MVN) install:install-file -Dfile=${junixsocket} -DgroupId=scaledmarkets -DartifactId=junixsocket-common-modified -Dversion=0.1 -Dpackaging=jar

# ------------------------------------------------------------------------------
# Build the various four components.

compile:
	$(MVN) install


# Create parser.
parser: $(jar_dir) $(parser_build_dir)
	rm -rf parser/java/*
	$(MVN) clean install --projects parser


# Create the common module that is shared by all components.
common: $(jar_dir) $(common_build_dir)
	$(MVN) clean install --projects common


# Create the end user command line application.
client: $(jar_dir) $(client_build_dir)
	$(MVN) clean install --projects client


# Create the container image that is invoked by the command line application.
task_runtime: $(jar_dir) $(task_runtime_build_dir)
	$(MVN) clean install --projects task_runtime


# Build and push container image for task runtime.
image:
	cp $(jar_dir)/*.jar taskruntime
	. $(DockerhubCredentials)
	docker build --no-cache --file taskruntime/Dockerfile --tag=$(TASK_RUNTIME_IMAGE_NAME) taskruntime
	@docker login -u $(DockerhubUserId) -p $(DockerhubPassword)
	docker push $(TASK_RUNTIME_IMAGE_NAME)
	docker logout


# ------------------------------------------------------------------------------
# Tests
#	Gherkin tags: done, smoke, notdone, docker, exec, unit, pushlocalrepo, task,
#	patternsets, inputsandoutputs

# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	echo "\n         namespace simple import abc      \n" > simple.dabl
	$(JAVA) -classpath $(client_compile_cp) scaledmarkets.dabl.Main -t simple.dabl

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
	$(test) --tags @done

test_smoke:
	$(test) --tags @smoke

test_unit:
	$(test) --tags @unit

test_psets:
	$(test) --tags @patternsets

test_iao:
	$(test) --tags @inputsandoutputs

test_pushlocalrepo:
	$(test) --tags @pushlocalrepo

test_docker:
	$(test) --tags @docker --tags @done

test_exec:
	$(test) --tags @exec

test_task:
	$(test) --tags @task --tags @done

test_check:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar \
		cucumber.api.cli.Main \
		$(test_src_dir)/features \

test_clean:
	rm -r -f $(test_build_dir)/*

cukehelp:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --help

cukever:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --version

clean: compile_clean test_clean clean_parser clean_task_parser
	rm -f Manifest
	rm -r -f $(javadoc_dir)/*

info:
	@echo "Makefile for $(PRODUCT_NAME)"
