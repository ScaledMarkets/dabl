# Environment configurations for makefile.
# Edit this file to specify the locations of required tools and build directory.

# Configurations particular to why this build is being run:
maxerrs = 5
.DELETE_ON_ERROR:
.DEFAULT_GOAL: compile

# Versions produced by this build:
DABL_VERSION = 0.1
BUILD_TAG = 0
TASK_RUNTIME_IMAGE_NAME = registry.hub.docker.com/ScaledMarkets/taskruntime

# Output directories.
build_dir = /Transient/dabl
task_build_dir = /Transient/dabl_task
jar_dir = /Transient/dabl/jars

# Locations of tools:
Library := /Users/cliffordberg/Library
SONAR_RUNNER := $(Library)/sonar-runner-2.4/bin/sonar-runner
sable := $(Library)/sablecc-3.2
junit_dir := $(Library)/JUnit
log4jcore := $(Library)/apache-log4j-2.0.2-bin/log4j-core-2.0.2.jar
log4japi := $(Library)/apache-log4j-2.0.2-bin/log4j-1.2-api-2.0.2.jar
commonslogging12 := $(Library)/commons-logging-1.2/commons-logging-1.2.jar
jaxrs := $(Library)/jaxrs-ri/api/*
jaxrs := $(jaxrs):$(Library)/jaxrs-ri/lib/*
jaxrs := $(jaxrs):$(Library)/jaxrs-ri/ext/*
jersey := $(Library)/jersey/*
#apache_http := $(Library)/httpcomponents-client-4.4.6/lib/*
apache_http := $(Library)/httpcomponents-client-4.5.3/lib/*
junixsocket := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/lib/junixsocket.jar
junixsocketnative := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/classes
javaxjson := $(Library)/javax.json/*
CUCUMBER_CLASSPATH := $(Library)/Cucumber/cucumber-core-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/cucumber-java-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/cucumber-jvm-deps-1.0.3.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(Library)/Cucumber/gherkin-2.12.2.jar

# Java compatibility: Requires Java 8.
# For Mac:
JAVA_HOME := $(shell /usr/libexec/java_home)
#JAVA_HOME := /Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home
# For other than Mac:
#JAVA_HOME = ....
