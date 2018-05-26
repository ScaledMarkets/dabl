# Do not run this makefile alone. Can only be run from the main makefile.

.PHONY: all manifest config compile clean javadoc jar

all: clean compile jar #javadoc

# Create the manifest file for the JAR.
manifest:
	echo "Main-Class: $(client_main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME) Client" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(client_main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(client_src_dir)/$(package)/ClientConfig.java
	echo "public class ClientConfig {" >> $(client_src_dir)/$(package)/ClientConfig.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(client_src_dir)/$(package)/ClientConfig.java
	echo "}" >> $(client_src_dir)/$(package)/ClientConfig.java

compile: manifest config
	@echo "Compiling client-----------------------------------------------------"
	$(MVN) install --projects client
	cp $(ThisDir)/.dabl.properties $(client_build_dir)

clean:
	$(MVN) --projects client clean
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(CLIENT_JAR_NAME)" ]; then echo "ERROR: CLIENT_JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(CLIENT_JAR_NAME).jar

# Create the directory that will contain the javadocs.
$(javadoc_dir):
	mkdir $(javadoc_dir)

# Generate API docs (javadocs).
javadoc: $(javadoc_dir)
	rm -rf $(javadoc_dir)/*
	$(JAVADOC) -protected -d $(javadoc_dir) \
		-classpath $(client_compile_cp) \
		-sourcepath $(client_src_dir):$(sable_dabl_out_dir) \
		-subpackages $(package_name) \
		-exclude $(test_package_name)
	git add $(javadoc_dir)/
	git commit -am "Generated api docs"
	git push

jar: $(jar_dir)/$(CLIENT_JAR_NAME).jar

$(jar_dir)/$(CLIENT_JAR_NAME).jar: manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(CLIENT_JAR_NAME).jar Manifest .dabl.properties \
		-C $(client_build_dir) scaledmarkets
	rm Manifest

