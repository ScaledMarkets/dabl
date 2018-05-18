# Configurations for makefile.
# Edit this file to specify the paths and versions of required tools and directories.

ifdef env
	include $(env)
else
	include env.mac
endif

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
export jar_dir = /Transient/dabl/jars

# User executing this script.
UserHome := $(HOME)

# Locations of dev tools:
export DevLibrary := $(UserHome)/Library
export sable := $(DevLibrary)/sablecc-3.2
jaxrs := $(DevLibrary)/jaxrs-ri/api/*
jaxrs := $(jaxrs):$(DevLibrary)/jaxrs-ri/lib/*
jaxrs := $(jaxrs):$(DevLibrary)/jaxrs-ri/ext/*
export jaxrs
export jersey := $(DevLibrary)/jersey/*
export apache_http := $(DevLibrary)/httpcomponents-client-4.5.3/lib/*
export junixsocket := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/lib/junixsocket.jar
export junixsocketnative := /Users/cliffordberg/Documents/ScaledMarkets/DABL/junixsocket/classes
export javaxjson := $(DevLibrary)/javax.json/*
CUCUMBER_CLASSPATH := $(DevLibrary)/Cucumber/cucumber-core-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(DevLibrary)/Cucumber/cucumber-java-1.1.8.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(DevLibrary)/Cucumber/cucumber-jvm-deps-1.0.3.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(DevLibrary)/Cucumber/gherkin-2.12.2.jar
export CUCUMBER_CLASSPATH

# Java compatibility: Requires Java 8.
# For Mac:
export JAVA_HOME := $(shell /usr/libexec/java_home)
#export JAVA_HOME := /Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home
# For other than Mac:
#export JAVA_HOME = ....
