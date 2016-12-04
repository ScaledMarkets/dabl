DABL_VERSION=1.0
BUILD_TAG=0
ORG = Scaled Markets
PRODUCT_NAME = Dabl
JAR_NAME = dabl

package=scaledmarkets/dabl
package_name = scaledmarkets.dabl
main_class = $(package_name).main.Dabl
test_package_name = scaledmarkets.dabl.test

# Configurations particular to why this build is being run:
maxerrs=5
.DELETE_ON_ERROR:
.ONESHELL:
.SUFFIXES:
.DEFAULT_GOAL: all

# Command aliases:
SHELL = /bin/sh
JAVAC = javac
JAVA = java
JAR = jar

# Project file locations:
CurDir = $(shell pwd)
src_dir = $(CurDir)/java
Userdir=/Users/cliffordberg
build_dir=/Transient/dabl
jar_dir = /Transient/dabl/jars
test_src_dir = $(CurDir)/test
test_build_dir = $(CurDir)/buildtest
test_package = scaledmarkets/dabl/test
testsourcefiles = $(test_src_dir)/$(test_package)/*.java
testclassfiles = $(test_build_dir)/$(test_package)/*.class $(test_build_dir)/$(test_package)/gen/*.class

# Locations of tools:
third_party_unzip_dir = /Transient/dabl/thirdpartyunzip
SONAR_RUNNER=/Users/cliffordberg/Library/sonar-runner-2.4/bin/sonar-runner
JAVA_HOME=$(shell /usr/libexec/java_home)
#JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home
EC2_HOME=$(Userdir)/Library/ec2-api-tools-1.7.4.0
Library=$(Userdir)/Library
sable=$(Library)/sablecc-3.2
sable_out_dir=$(CurDir)/SableCCOutput
junit_dir=Userdir/Library/JUnit
aws_jar=$(Userdir)/Library/aws-java-sdk-1.10.0/lib/aws-java-sdk-1.10.0.jar
aws_3rdParty_dir=$(Userdir)/Library/aws-java-sdk-1.10.0/third-party
ectools=$(Userdir)/Library/ec2-api-tools-1.7.4.0/bin
log4jcore=$(Userdir)/Library/apache-log4j-2.0.2-bin/log4j-core-2.0.2.jar
log4japi=$(Userdir)/Library/apache-log4j-2.0.2-bin/log4j-1.2-api-2.0.2.jar
commonslogging12=$(Userdir)/Library/commons-logging-1.2/commons-logging-1.2.jar
#jacksondir=$(Userdir)/Library/jackson-2.5.4
#PATH=$(PATH):$(ectools)
aws_3rdParty=$(aws_3rdParty_dir)/jackson-databind-2.5.3/jackson-databind-2.5.3.jar:$(aws_3rdParty_dir)/jackson-annotations-2.5.3/jackson-annotations-2.5.3.jar:$(aws_3rdParty_dir)/jackson-core-2.5.3/jackson-core-2.5.3.jar:$(aws_3rdParty_dir)/commons-logging-1.1.3/commons-logging-1.1.3.jar:$(aws_3rdParty_dir)/httpcomponents-client-4.3/httpcore-4.3.3.jar:$(aws_3rdParty_dir)/httpcomponents-client-4.3/httpclient-4.3.6.jar:$(aws_3rdParty_dir)/joda-time-2.8/joda-time-2.8.jar

# Java classpaths:
buildcp=$(build_dir):$(aws_jar)
runcp=$(build_dir):$(aws_jar):$(aws_3rdParty)
test_compile_cp = $(junit_dir)/*:$(test_build_dir):$(build_dir)
test_cp = $(junit_dir)/*:$(test_build_dir):$(build_dir)

# Intermediate artifacts:
sourcefiles = $(src_dir)/$(package)/*.java \
	$(src_dir)/$(package)/main/*.java \
	$(src_dir)/$(package)/provider/*.java \
	$(src_dir)/$(package)/gen/*.java \
	$(src_dir)/$(package)/test/gen/*.java
classfiles = $(build_dir)/$(package)/*.class \
	$(src_dir)/$(package)/main/*.class \
	$(src_dir)/$(package)/provider/*.class \
	$(src_dir)/$(package)/gen/*.class \
	$(src_dir)/$(package)/test/gen/*.class
