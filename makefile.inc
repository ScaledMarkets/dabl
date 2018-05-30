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
export build_dir = $(HOME)/Transient/dabl
export jar_dir = $(HOME)/Transient/dabl/jars

# User executing this script.
export UserHome := $(HOME)

# Locations of dev tools:
export MVN_REPO := $(HOME)/.m2/repository
#export sable := $(DevLibrary)/sablecc-3.6
jaxrs := $(MVN_REPO)/org/glassfish/jersey/bundles/jaxrs-ri/2.27/jaxrs-ri-2.27.jar
export jaxrs := $(jaxrs):$(MVN_REPO)/org/glassfish/jersey/connectors/jersey-apache-connector/2.27/jersey-apache-connector-2.27.jar
export apache_http := $(MVN_REPO)/org/apache/httpcomponents/httpclient/4.5.5/httpclient-4.5.5.jar
export javaxjson := $(MVN_REPO)/javax/json/javax.json-api/1.1.2/javax.json-api-1.1.2.jar
#export jersey := $(DevLibrary)/jersey/*
CUCUMBER_CLASSPATH := $(MVN_REPO)/info/cukes/cucumber-java/1.2.5/cucumber-java-1.2.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/cucumber-core/1.2.5/cucumber-core-1.2.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/cucumber-jvm-deps/1.0.5/cucumber-jvm-deps-1.0.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/gherkin/2.12.2/gherkin-2.12.2.jar
export CUCUMBER_CLASSPATH
