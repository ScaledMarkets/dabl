# Configurations for makefile.
# Edit this file to specify the paths and versions of required tools and directories.

export os := $(shell uname)

ifeq ($(os),Darwin)
	include env.mac
endif

ifeq ($(os),Linux)
	include env.linux
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
export build_dir = $(Transient)
export jar_dir = $(Transient)/jars

# User executing this script.
export UserHome := $(HOME)

# Locations of dev tools:
export MVN_REPO := $(HOME)/.m2/repository
#export sable := $(DevLibrary)/sablecc-3.6
export jaxrs_ri := $(MVN_REPO)/org/glassfish/jersey/bundles/jaxrs-ri/2.27/jaxrs-ri-2.27.jar
export jersey_common := $(MVN_REPO)/org/glassfish/jersey/core/jersey-common/2.27/jersey-common-2.27.jar
export jersey_client := $(MVN_REPO)/org/glassfish/jersey/core/jersey-client/2.21/jersey-client-2.21.jar
#export jersey_client := $(MVN_REPO)/org/glassfish/jersey/core/jersey-client/2.27/jersey-client-2.27.jar
export jersey_grizzly_connector := $(MVN_REPO)/org/glassfish/jersey/connectors/jersey-grizzly-connector/2.21/jersey-grizzly-connector-2.21.jar
export jersey_apache_connector := $(MVN_REPO)/org/glassfish/jersey/connectors/jersey-apache-connector/2.21/jersey-apache-connector-2.21.jar
export jersey_jetty_connector := $(MVN_REPO)/org/glassfish/jersey/connectors/jersey-jetty-connector/2.21/jersey-jetty-connector-2.21.jar
export apache_http_client := $(MVN_REPO)/org/apache/httpcomponents/httpclient/4.5.5/httpclient-4.5.5.jar
export apache_http_core := $(MVN_REPO)/org/apache/httpcomponents/httpcore/4.4.9/httpcore-4.4.9.jar
export commons_logging := $(MVN_REPO)/commons-logging/commons-logging/1.2/commons-logging-1.2.jar
export javax_annotation := $(MVN_REPO)/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar
export hk2 := $(MVN_REPO)/org/glassfish/hk2/hk2/2.4.0/hk2-2.4.0.jar
export hk2_api := $(MVN_REPO)/org/glassfish/hk2/hk2-api/2.4.0/hk2-api-2.4.0.jar
export hk2_utils := $(MVN_REPO)/org/glassfish/hk2/hk2-utils/2.5.0-b61/hk2-utils-2.5.0-b61.jar
export hk2_locator := $(MVN_REPO)/org/glassfish/hk2/hk2-locator/2.4.0/hk2-locator-2.4.0.jar
export javassist := $(MVN_REPO)/org/javassist/javassist/3.22.0-GA/javassist-3.22.0-GA.jar
export javaxjson := $(MVN_REPO)/javax/json/javax.json-api/1.1.2/javax.json-api-1.1.2.jar
export javax_ws := $(MVN_REPO)/javax/ws/rs/javax.ws.rs-api/2.1/javax.ws.rs-api-2.1.jar
export javax_json_glassfish := $(MVN_REPO)/org/glassfish/javax.json/1.1.2/javax.json-1.1.2.jar
export jersey_inject := $(MVN_REPO)/org/glassfish/jersey/inject/jersey-hk2/2.27/jersey-hk2-2.27.jar
export javax_named := $(MVN_REPO)/javax/inject/javax.inject/1/javax.inject-1.jar





jersey := ""
jersey := $(jersey):$(jersey_client)
jersey := $(jersey):$(jersey_connector)
jersey := $(jersey):$(jersey_apache_connector)
jersey := $(jersey):$(jersey_jetty_connector)

export jersey
#export jersey := $(jersey_connector):$(javax_json_glassfish):$(jersey_inject):$(javax_named):$(apache_http_client):$(apache_http_core):$(jersey_client):$(jersey_common):$(hk2):$(hk2_api):$(hk2_utils):$(hk2_locator):$(javassist):$(commons_logging):$(javax_annotation):$(javaxjson)


CUCUMBER_CLASSPATH := $(MVN_REPO)/info/cukes/cucumber-java/1.2.5/cucumber-java-1.2.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/cucumber-core/1.2.5/cucumber-core-1.2.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/cucumber-jvm-deps/1.0.5/cucumber-jvm-deps-1.0.5.jar
CUCUMBER_CLASSPATH := $(CUCUMBER_CLASSPATH):$(MVN_REPO)/info/cukes/gherkin/2.12.2/gherkin-2.12.2.jar
export CUCUMBER_CLASSPATH
