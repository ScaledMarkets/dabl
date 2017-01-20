# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc


# Artifact names:
ORG = Scaled Markets
PRODUCT_NAME = Dabl
JAR_NAME = dabl

# Output artifact names:
package=scaledmarkets/dabl
test_package=scaledmarkets/dabl/test
package_name = scaledmarkets.dabl
test_package_name = scaledmarkets.dabl.test
main_class := $(package_name).main.Dabl

# Intermediate artifacts:
sourcefiles := $(src_dir)/$(package)/main/*.java \
	./SableCCOutput/analysis/*.java \
	./SableCCOutput/lexer/*.java \
	./SableCCOutput/node/*.java \
	./SableCCOutput/parser/*.java
classfiles := \
	$(build_dir)/$(package)/main/*.class \
	$(build_dir)/$(package)/analysis/*.class \
	$(build_dir)/$(package)/lexer/*.class \
	$(build_dir)/$(package)/node/*.class \
	$(build_dir)/$(package)/parser/*.class

# Configurations particular to why this build is being run:
maxerrs = 5
.DELETE_ON_ERROR:
.ONESHELL:
.SUFFIXES:
.DEFAULT_GOAL: compile

# Command aliases:
SHELL = /bin/sh
JAVAC = javac
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
testclassfiles := $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/gen/*.class
sable_out_dir := $(CurDir)/SableCCOutput
javadoc_dir := $(CurDir)/docs

# Java classpaths:
buildcp := $(build_dir)
compile_tests_cp := $(CUCUMBER_CLASSPATH):$(buildcp)
test_cp := $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar


################################################################################
# Tasks
.PHONY: all manifest config gen_parser compile_parser parser jar compile dist \
	check compile_tests test runsonar javadoc clean info

all: clean parser compile check jar compile_tests test javadoc

# Create the manifest file for the JAR.
manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME)" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

# Generate the DABL parser.
# Generates dabl compiler tables and classes.
gen_parser: dabl.sablecc $(sable_out_dir) $(build_dir)
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_out_dir) --no-inline dabl.sablecc

# Compile the generated code.
compile_parser: gen_parser
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp) -d $(build_dir) \
		$(sable_out_dir)/$(package)/node/*.java \
		$(sable_out_dir)/$(package)/lexer/*.java \
		$(sable_out_dir)/$(package)/analysis/*.java \
		$(sable_out_dir)/$(package)/parser/*.java
	cp $(sable_out_dir)/$(package)/lexer/lexer.dat $(build_dir)/$(package)/lexer
	cp $(sable_out_dir)/$(package)/parser/parser.dat $(build_dir)/$(package)/parser

# Generate and compile the parser classs.
parser: compile_parser

# Create the directory into which the generated parser source files will be placed.
$(sable_out_dir):
	mkdir $(sable_out_dir)

# Create the directory that will contain the compiled class files.
$(build_dir):
	mkdir $(build_dir)

# Create the directory that will contain the compiled test class files.
$(test_build_dir):
	mkdir $(test_build_dir)

# Create the directory that will contain the packaged application (JAR file).
$(jar_dir):
	mkdir $(jar_dir)

# Package application into a JAR file.
jar: $(jar_dir)/$(JAR_NAME).jar

$(jar_dir)/$(JAR_NAME).jar: $(classfiles) manifest $(jar_dir) $(jar_dir)
	$(JAR) cvfm $(jar_dir)/$(JAR_NAME).jar Manifest -C $(build_dir) scaledmarkets
	rm Manifest

# Define the 'compile' target so that we can reference it in .DEFAULT_GOAL.
compile:
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp) -d $(build_dir) \
		$(src_dir)/sablecc/*.java \
		$(src_dir)/$(package)/*.java \
		$(src_dir)/$(package)/main/*.java

# Define 'dist' target so we can reference it in 'all' target.
dist: jar

# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	echo "\n         namespace simple import abc      \n" > simple.dabl
	$(JAVA) -classpath $(build_dir) scaledmarkets.dabl.main.Dabl -t simple.dabl

# Compile the test source files.
compile_tests: $(test_build_dir)
	javac -cp $(compile_tests_cp) -d $(test_build_dir) \
		$(test_src_dir)/steps/$(test_package)/*.java

# Run Cucumber tests.
test:
	java -cp $(CUCUMBER_CLASSPATH):$(test_build_dir):$(jar_dir)/$(JAR_NAME).jar \
		cucumber.api.cli.Main \
		--glue scaledmarkets.dabl.test $(test_src_dir)/features \
		--tags @done

# Perform code quality scans.
runsonar:
	$(SONAR_RUNNER)

# Create the directory that will contain the javadocs.
$(javadoc_dir):
	mkdir $(javadoc_dir)

# Generate API docs (javadocs).
javadoc: $(javadoc_dir)
	rm -rf $(javadoc_dir)/*
	$(JAVADOC) -protected -d $(javadoc_dir) \
		-classpath $(build_dir) \
		-sourcepath $(src_dir):$(sable_out_dir) \
		-subpackages $(package_name) \
		-exclude $(test_package_name)
	git add $(javadoc_dir)/
	git commit -am "Generated api docs"
	git push

clean:
	rm -r -f $(build_dir)
	rm -r -f $(jar_dir)
	rm -r -f $(sable_out_dir)/*
	rm -f Manifest
	rm -r -f $(javadoc_dir)

info:
	@echo "Makefile for $(PRODUCT_NAME)"
