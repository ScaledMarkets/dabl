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
export ThirdPartyJarDir := $(jar_dir)/third_party
export sable_dabl_out_dir := $(ThisDir)/parser/java
export common_src_dir := $(ThisDir)/common/java
export client_src_dir := $(ThisDir)/client/java
export task_runtime_src_dir := $(ThisDir)/task_runtime/java
export test_src_dir := $(ThisDir)/test
export test_package = $(package)/test
export javadoc_dir := $(ThisDir)/docs
export third_party_cp := $(junixsocket):$(javax_ws):$(jersey)
export parser_jar := $(MVN_REPO)/scaledmarkets/parser/$(DABL_VERSION)/parser-$(DABL_VERSION).jar
export common_jar := $(MVN_REPO)/scaledmarkets/common/$(DABL_VERSION)/common-$(DABL_VERSION).jar
export client_jar := $(MVN_REPO)/scaledmarkets/client/$(DABL_VERSION)/client-$(DABL_VERSION).jar
export task_runtime_jar := $(MVN_REPO)/scaledmarkets/task_runtime/$(DABL_VERSION)/task_runtime-$(DABL_VERSION).jar
export test_jar := $(MVN_REPO)/scaledmarkets/test/$(DABL_VERSION)/test-$(DABL_VERSION).jar
export client_cp := $(client_jar):$(common_jar):$(parser_jar)

# Aliases:
test := java -cp $(CUCUMBER_CLASSPATH):$(test_jar):$(client_cp):$(ThirdPartyJarDir)/*
test := $(test) -Djava.library.path=$(junixsocketnative)
test := $(test) cucumber.api.cli.Main --glue scaledmarkets.dabl.test
test := $(test) $(test_src_dir)/features


################################################################################
# Tasks


.PHONY: all jars parser common client task_runtime \
	manifest config gen_parser compile_parser parser jar compile dist \
	task_manifest task_jar image \
	check compile_tests test runsonar javadoc clean_parser clean_task_parser info

all: gen_config jars image

mvnversion:
	$(MVN) --version


# ------------------------------------------------------------------------------
# Create the directory that will contain the parser source files.
$(sable_dabl_out_dir):
	mkdir -p $(sable_dabl_out_dir)/$(package)

# ------------------------------------------------------------------------------
# Create the directories that will contain the compiled class files.
$(build_dir):
	mkdir -p $(build_dir)

$(parser_build_dir):
	mkdir -p $(parser_build_dir)

$(common_build_dir):
	mkdir -p $(common_build_dir)

$(client_build_dir):
	mkdir -p $(client_build_dir)

$(task_runtime_build_dir):
	mkdir -p $(task_runtime_build_dir)

# ------------------------------------------------------------------------------
# Create the directory that will contain the jar files that are created.
$(jar_dir):
	mkdir -p $(jar_dir)

# ------------------------------------------------------------------------------
# Create directory for third party jars - needed to assemble jars for image.
$(ThirdPartyJarDir):
	mkdir -p $(ThirdPartyJarDir)


# ------------------------------------------------------------------------------
# Install junixsocket - needed by common. It is assumed that junixsocket is
# located at ${junixsocket}. Note that this fork of junixsocket does not use
# the NAR system.
install_junixsocket:
	$(MVN) install:install-file -Dfile=$(junixsocket) -DgroupId=scaledmarkets -DartifactId=junixsocket-common-modified -Dversion=0.1 -Dpackaging=jar

# ------------------------------------------------------------------------------
# Generate the Config class that the runtime uses to determine the version of DABL.
gen_config:
	echo "package scaledmarkets.dabl;" > $(common_src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(common_src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(common_src_dir)/$(package)/Config.java
	echo "}" >> $(common_src_dir)/$(package)/Config.java

# ------------------------------------------------------------------------------
# Build all four components.
jars:
	$(MVN) install

# For development: Create only the parser.
parser: $(jar_dir) $(parser_build_dir)
	rm -rf parser/java/*
	$(MVN) clean install --projects parser


# For development: Create only the common module that is shared by all components.
common: $(jar_dir) $(common_build_dir)
	$(MVN) clean install --projects common


# For development: Create only the end user command line application.
client: $(jar_dir) $(client_build_dir)
	$(MVN) clean install --projects client


# For development: Create only the runtime that is invoked by the command line application.
task_runtime: $(jar_dir) $(task_runtime_build_dir)
	$(MVN) clean install --projects task_runtime


# For development: Compile only the behavioral tests.
test: $(jar_dir) $(task_runtime_build_dir)
	$(MVN) clean install --projects test

# ------------------------------------------------------------------------------
# Generate javadocs for all modules.
javadoc:
	$(MVN) javadoc:javadoc


showdeps:
	$(MVN) dependency:build-classpath --projects client | tail -n $(mvn_spaces) | head -n 1 | tr ":" "\n" > client_jars.txt
	$(MVN) dependency:build-classpath --projects common | tail -n $(mvn_spaces) | head -n 1 | tr ":" "\n" > common_jars.txt
	sort -u client_jars.txt common_jars.txt

# ------------------------------------------------------------------------------
# Identify the jar files that need to be added to the image.
copydeps: $(ThirdPartyJarDir)
	# Note: Use 'mvn dependency:build-classpath' to obtain dependencies.
	$(MVN) dependency:build-classpath --projects client | tail -n $(mvn_spaces) | head -n 1 | tr ":" "\n" > client_jars.txt
	$(MVN) dependency:build-classpath --projects common | tail -n $(mvn_spaces) | head -n 1 | tr ":" "\n" > common_jars.txt
	{ \
	cp=`sort -u client_jars.txt common_jars.txt` ; \
	for path in $$cp; do cp $$path $$ThirdPartyJarDir; done; \
	}


# ------------------------------------------------------------------------------
# Build and push container image for task runtime.
# This must be run on a machine with docker.
# It is assumed that Dockerhub credentials have been added to the shell env.
image:
	rm -f build-taskruntime/*
	cp $(HOME)/.m2/repository/scaledmarkets/parser/$(DABL_VERSION)/parser-$(DABL_VERSION).jar build-taskruntime/parser.jar
	cp $(HOME)/.m2/repository/scaledmarkets/common/$(DABL_VERSION)/common-$(DABL_VERSION).jar build-taskruntime/common.jar
	cp $(HOME)/.m2/repository/scaledmarkets/task_runtime/$(DABL_VERSION)/task_runtime-$(DABL_VERSION).jar build-taskruntime/task_runtime.jar
	cp $(HOME)/.m2/repository/scaledmarkets/junixsocket-common-modified/0.1/junixsocket-common-modified-0.1.jar build-taskruntime/junixsocket.jar
	cp task_runtime/Dockerfile build-taskruntime
	if [ ! -n $$DockerhubUserId ] || [ ! -n $$DockerhubPassword ]; then { echo "Dockerhub credentials not set"; exit; } fi
	. $(DockerhubCredentials)
	sudo docker build --no-cache --tag=$(TASK_RUNTIME_IMAGE_NAME) build-taskruntime
	sudo docker login -u $$DockerhubUserId -p $$DockerhubPassword
	sudo docker push $(TASK_RUNTIME_IMAGE_NAME)
	sudo docker logout
	rm build-taskruntime/*


# ------------------------------------------------------------------------------
# Tests
#	Gherkin tags: done, smoke, notdone, docker, exec, unit, pushlocalrepo, task,
#	patternsets, inputsandoutputs

# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	echo "\n         namespace simple import abc      \n" > simple.dabl
	$(JAVA) -classpath $(client_compile_cp) scaledmarkets.dabl.Main -t simple.dabl

# Run Cucumber tests.
# Note: We could export LD_LIBRARY_PATH instead of passing it in the java command.
test_all:
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
	sudo $(test) --tags @task --tags @done

cukehelp:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --help

cukever:
	java -cp $(CUCUMBER_CLASSPATH) cucumber.api.cli.Main --version

clean: compile_clean test_clean clean_parser clean_task_parser
	rm -f Manifest
	rm -r -f $(javadoc_dir)/*

info:
	@echo "Makefile for $(PRODUCT_NAME)"
