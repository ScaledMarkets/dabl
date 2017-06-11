# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	main_class, PRODUCT_NAME, DABL_VERSION, ORG, task_main_class, BUILD_TAG,
#	jar_dir, TASK_JAR_NAME, task_classfiles, JAR, task_build_dir, TASK_RUNTIME_IMAGE_NAME

# Intermediate artifacts:
export task_classfiles := \
	$(build_dir)/$(package)/task/*.class


# Create the manifest file for the task JAR.
task_manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME) Task Execution Engine" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(task_main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Package task runtime into a JAR file.
taskjar: $(jar_dir)/$(TASK_JAR_NAME).jar

$(jar_dir)/$(TASK_JAR_NAME).jar: $(task_classfiles) task_manifest $(jar_dir)
	$(JAR) cvfm $(jar_dir)/$(TASK_JAR_NAME).jar Manifest -C $(task_build_dir) scaledmarkets
	rm Manifest

# Build container image for task runtime.
image:
	docker build --file Dockerfile --tag=$TASK_RUNTIME_IMAGE_NAME .
	....push image to scaled markets image registry
