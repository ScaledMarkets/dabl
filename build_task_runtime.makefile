# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	DockerhubUserId, DockerhubPassword,
#	main_class, PRODUCT_NAME, DABL_VERSION, ORG, task_main_class, BUILD_TAG,
#	jar_dir, TASK_JAR_NAME, task_classfiles, JAR, task_build_dir, IMAGE_REGISTRY,
#	TASK_RUNTIME_IMAGE_NAME

# Intermediate artifacts:
export task_classfiles := \
	$(client_build_dir)/$(package)/*.class \
	$(client_build_dir)/$(package)/analysis/*.class \
	$(client_build_dir)/$(package)/analyzer/*.class \
	$(client_build_dir)/$(package)/docker/*.class \
	$(client_build_dir)/$(package)/exec/*.class \
	$(client_build_dir)/$(package)/handlers/*.class \
	$(client_build_dir)/$(package)/helper/*.class \
	$(client_build_dir)/$(package)/lexer/*.class \
	$(client_build_dir)/$(package)/node/*.class \
	$(client_build_dir)/$(package)/parser/*.class \
	$(client_build_dir)/$(package)/repos/*.class \
	$(client_build_dir)/$(package)/task/*.class \
	$(client_build_dir)/$(package)/util/*.class

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
taskjar: $(jar_dir)/$(TASK_JAR_NAME).jar $(jar_dir)

$(jar_dir)/$(TASK_JAR_NAME).jar: $(task_classfiles) task_manifest $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(TASK_JAR_NAME).jar Manifest -C $(task_build_dir) scaledmarkets
	rm Manifest

# Build and push container image for task runtime.
image:
	docker build --file Dockerfile --tag=$(TASK_RUNTIME_IMAGE_NAME) .
	docker login -u $(DockerhubUserId) -p $(DockerhubPassword) $(IMAGE_REGISTRY)
	docker push $(TASK_RUNTIME_IMAGE_NAME)
	docker logout $(IMAGE_REGISTRY)
