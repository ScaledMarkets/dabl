# Do not run this makefile alone. Can only be run from the main makefile.

.PHONY: all task_manifest compile jar

all: clean compile jar

# Create the manifest file for the task JAR.
manifest:
	echo "Specification-Title: $(PRODUCT_NAME) Common Modules" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: Common Modules" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(client_src_dir)/$(package)/CommonConfig.java
	echo "public class CommonConfig {" >> $(client_src_dir)/$(package)/CommonConfig.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(client_src_dir)/$(package)/CommonConfig.java
	echo "}" >> $(client_src_dir)/$(package)/CommonConfig.java

compile: manifest config
	@echo "Compiling common-----------------------------------------------------"
	$(MVN) install --projects common

clean:
	$(MVN) --projects common clean
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(COMMON_JAR_NAME)" ]; then echo "ERROR: COMMON_JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(COMMON_JAR_NAME).jar

# Package task runtime into a JAR file.

jar: $(jar_dir)/$(COMMON_JAR_NAME).jar

$(jar_dir)/$(COMMON_JAR_NAME).jar: task_manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(COMMON_JAR_NAME).jar Manifest \
		-C $(common_build_dir) scaledmarkets
	rm Manifest

