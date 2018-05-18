# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	DockerhubUserId, DockerhubPassword,
#	main_class, PRODUCT_NAME, DABL_VERSION, ORG, task_main_class, BUILD_TAG,
#	jar_dir, TASK_JAR_NAME, task_classfiles, JAR, task_runtime_build_dir, 
#	task_runtime_compile_cp, IMAGE_REGISTRY, TASK_RUNTIME_IMAGE_NAME

all: image

# Create the manifest file for the task JAR.
task_manifest:
	echo "Main-Class: $(task_main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME) Task Execution Engine" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(task_main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# 
compile: $(task_runtime_build_dir)
	$(MVN) compile --projects take_runtime
	cp $(CurDir)/.dabl.container.properties $(task_runtime_build_dir)

# Package task runtime into a JAR file.
taskjar: task_manifest $(jar_dir)/$(TASK_JAR_NAME).jar $(jar_dir)

$(jar_dir)/$(TASK_JAR_NAME).jar: task_manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(TASK_JAR_NAME).jar Manifest \
		-C $(task_runtime_build_dir) scaledmarkets
	$(JAR) uf $(jar_dir)/$(TASK_JAR_NAME).jar \
		-C $(task_runtime_build_dir) scaledmarkets
	$(JAR) uf $(jar_dir)/$(TASK_JAR_NAME).jar \
		-C $(parser_build_dir) scaledmarkets
	$(JAR) uf $(jar_dir)/$(TASK_JAR_NAME).jar \
		-C $(task_runtime_build_dir) dabl
	rm Manifest

# Build and push container image for task runtime.
image: taskjar
	cp $(jar_dir)/$(TASK_JAR_NAME).jar taskruntime
	. $(DockerhubCredentials)
	docker build --no-cache --file taskruntime/Dockerfile --tag=$(TASK_RUNTIME_IMAGE_NAME) taskruntime
	@docker login -u $(DockerhubUserId) -p $(DockerhubPassword)
	docker push $(TASK_RUNTIME_IMAGE_NAME)
	docker logout
