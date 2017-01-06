# Environment configurations for makefile.

DABL_VERSION=0.1
BUILD_TAG=0
ORG = Scaled Markets
PRODUCT_NAME = Dabl
JAR_NAME = dabl

package=scaledmarkets/dabl
package_name = scaledmarkets.dabl
main_class := $(package_name).main.Dabl

# Configurations particular to why this build is being run:
maxerrs=5
.DELETE_ON_ERROR:
.ONESHELL:
.SUFFIXES:
.DEFAULT_GOAL: compile

# Command aliases:
SHELL = /bin/sh
JAVAC = javac
JAVA = java
JAR = jar

# Project file locations:
CurDir := $(shell pwd)
src_dir := $(CurDir)/java
Userdir=/Users/cliffordberg
build_dir=/Transient/dabl
jar_dir = /Transient/dabl/jars
test_src_dir := $(CurDir)/test
test_build_dir := $(CurDir)/buildtest
test_package = $(package)/test
testsourcefiles := $(test_src_dir)/$(test_package)/*.java
testclassfiles := $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/gen/*.class

# Locations of tools:
SONAR_RUNNER=$(Userdir)/Library/sonar-runner-2.4/bin/sonar-runner
JAVA_HOME:=$(shell /usr/libexec/java_home)
#JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home
Library:=$(Userdir)/Library
sable:=$(Library)/sablecc-3.2
sable_out_dir:=$(CurDir)/SableCCOutput
junit_dir=Userdir/Library/JUnit
log4jcore=:$(Userdir)/Library/apache-log4j-2.0.2-bin/log4j-core-2.0.2.jar
log4japi:=$(Userdir)/Library/apache-log4j-2.0.2-bin/log4j-1.2-api-2.0.2.jar
commonslogging12:=$(Userdir)/Library/commons-logging-1.2/commons-logging-1.2.jar
#jacksondir:=$(Userdir)/Library/jackson-2.5.4
#PATH:=$(PATH):$(ectools)
CUCUMBER_CLASSPATH:=$(Userdir)/Library/Cucumber/cucumber-core-1.1.8.jar
CUCUMBER_CLASSPATH:=$(CUCUMBER_CLASSPATH):$(Userdir)/Library/Cucumber/cucumber-java-1.1.8.jar
CUCUMBER_CLASSPATH:=$(CUCUMBER_CLASSPATH):$(Userdir)/Library/Cucumber/cucumber-jvm-deps-1.0.3.jar
CUCUMBER_CLASSPATH:=$(CUCUMBER_CLASSPATH):$(Userdir)/Library/Cucumber/gherkin-2.12.2.jar

# Java classpaths:
buildcp:=$(build_dir)
runcp:=$(build_dir):$(aws_jar)
test_compile_cp := $(junit_dir)/*:$(test_build_dir):$(build_dir)
test_cp := $(junit_dir)/*:$(test_build_dir):$(build_dir)

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
