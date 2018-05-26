# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	DockerhubUserId, DockerhubPassword,
#	main_class, PRODUCT_NAME, DABL_VERSION, ORG, task_main_class, BUILD_TAG,
#	jar_dir, TASK_JAR_NAME, task_classfiles, JAR, task_runtime_build_dir, 
#	task_runtime_compile_cp, IMAGE_REGISTRY, TASK_RUNTIME_IMAGE_NAME

.PHONY: all manifest config compile clean javadoc jar

all: clean compile jar

# Create the manifest file for the task JAR.
manifest:
	echo "Main-Class: $(task_main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME) Task Execution Engine" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(task_main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(client_src_dir)/$(package)/TaskRuntimeConfig.java
	echo "public class TaskRuntimeConfig {" >> $(client_src_dir)/$(package)/TaskRuntimeConfig.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(client_src_dir)/$(package)/TaskRuntimeConfig.java
	echo "}" >> $(client_src_dir)/$(package)/TaskRuntimeConfig.java

# 
compile: manifest config
	@echo "Compiling task runtime-----------------------------------------------------"
	$(MVN) install --projects take_runtime
	cp $(ThisDir)/.dabl.container.properties $(task_runtime_build_dir)

clean:
	$(MVN) --projects task_runtime clean
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(TASK_JAR_NAME)" ]; then echo "ERROR: TASK_JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(TASK_JAR_NAME).jar

# Package task runtime into a JAR file.
jar: task_manifest $(jar_dir)/$(TASK_JAR_NAME).jar $(jar_dir)

$(jar_dir)/$(TASK_JAR_NAME).jar: task_manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(TASK_JAR_NAME).jar Manifest \
		-C $(task_runtime_build_dir) scaledmarkets
	rm Manifest

