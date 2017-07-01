# Environment configurations for makefile.
# Edit this file to specify the locations of required tools and build directory.

# Configurations particular to why this build is being run:
export maxerrs = 5
.DELETE_ON_ERROR:
.DEFAULT_GOAL: compile

# Versions produced by this build:
export DABL_VERSION = 0.1
export BUILD_TAG = 0
export IMAGE_REGISTRY = registry.hub.docker.com
export TASK_RUNTIME_IMAGE_NAME = scaledmarkets/taskruntime

# Output directories.
export build_dir = /Transient/dabl
export task_build_dir = /Transient/dabl_task
export jar_dir = /Transient/dabl/jars

# Locations of tools:
export Library := /Users/cliffordberg/Library
export SONAR_RUNNER := $(Library)/sonar-runner-2.4/bin/sonar-runner
export sable := $(Library)/sablecc-3.2
export junit_dir := $(Library)/JUnit
export log4jcore := $(Library)/apache-log4j-2.0.2-bin/log4j-core-2.0.2.jar
export log4japi := $(Library)/apache-log4j-2.0.2-bin/log4j-1.2-api-2.0.2.jar
export commonslogging12 := $(Library)/commons-logging-1.2/commons-logging-1.2.jar
jaxrs := $(Library)/jaxrs-ri/api/*
jaxrs := $(jaxrs):$(Library)/jaxrs-ri/lib/*
jaxrs := $(jaxrs):$(Library)/jaxrs-ri/ext/*
export jaxrs
export jersey := $(Library)/jersey/*
#export apache_http := $(Library)/httpcomponents-client-4.4.6/lib/*
export apache_http := $(Library)/httpcomponents-client-4.5.3/lib/*
export junixsocket := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/lib/junixsocket.jar
export junixsocketnative := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/classes
export javaxjson := $(Library)/javax.json/*
CUCUMBER_CLASSPATH := $(Library)/Cucumber/cucumber-core-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/cucumber-java-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/cucumber-jvm-deps-1.0.3.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/gherkin-2.12.2.jar
export CUCUMBER_CLASSPATH

# Java compatibility: Requires Java 8.
# For Mac:
export JAVA_HOME := $(shell /usr/libexec/java_home)
#export JAVA_HOME := /Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home
# For other than Mac:
#export JAVA_HOME = ....
