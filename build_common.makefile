# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	DockerhubUserId, DockerhubPassword,
#	main_class, PRODUCT_NAME, DABL_VERSION, ORG, task_main_class, BUILD_TAG,
#	jar_dir, COMMON_JAR_NAME, task_classfiles, JAR, task_runtime_build_dir, 
#	task_runtime_compile_cp, IMAGE_REGISTRY, TASK_RUNTIME_IMAGE_NAME

.PHONY: all task_manifest compile jar

all: compile jar

# Create the manifest file for the task JAR.
task_manifest:
	echo "Specification-Title: $(PRODUCT_NAME) Common Modules" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: Common Modules" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

compile:
	$(MVN) compile --projects common

# Package task runtime into a JAR file.

jar: $(jar_dir)/$(COMMON_JAR_NAME).jar

$(jar_dir)/$(COMMON_JAR_NAME).jar: task_manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(COMMON_JAR_NAME).jar Manifest \
		-C $(common_build_dir) scaledmarkets
	rm Manifest
