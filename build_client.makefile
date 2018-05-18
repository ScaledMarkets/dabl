# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	JAVAC, maxerrs, client_compile_cp, third_party_cp, parser_build_dir, client_build_dir,
#	src_dir, package, CurDir,
#	jar_dir, main_class, PRODUCT_NAME, DABL_VERSION, ORG, BUILD_TAG, CLIENT_JAR_NAME,
#	classfiles, JAR, javadoc_dir, JAVADOC, src_dir, sable_dabl_out_dir, package_name

all: clean jar #javadoc

.PHONY: compile clean manifest javadoc

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl.client;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

compile: $(client_build_dir) manifest
	$(MVN) compile --projects client
	cp $(CurDir)/.dabl.properties $(client_build_dir)

clean:
	if [ -z "$(client_build_dir)" ]; then echo "ERROR: client_build_dir variable is not set"; exit 1; fi
	rm -r -f $(client_build_dir)/*
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(CLIENT_JAR_NAME)" ]; then echo "ERROR: CLIENT_JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(CLIENT_JAR_NAME).jar

# Create the manifest file for the JAR.
manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME)" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

$(jar_dir)/$(CLIENT_JAR_NAME).jar: manifest compile $(jar_dir)
	$(JAR) cfm $(jar_dir)/$(CLIENT_JAR_NAME).jar Manifest .dabl.properties \
		-C $(client_build_dir) scaledmarkets
	$(JAR) uf $(jar_dir)/$(CLIENT_JAR_NAME).jar \
		-C $(parser_build_dir) scaledmarkets
	$(JAR) uf $(jar_dir)/$(CLIENT_JAR_NAME).jar \
		-C $(client_build_dir) dabl
	rm Manifest

jar: $(jar_dir)/$(CLIENT_JAR_NAME).jar

# Create the directory that will contain the javadocs.
$(javadoc_dir):
	mkdir $(javadoc_dir)

# Generate API docs (javadocs).
javadoc: $(javadoc_dir)
	rm -rf $(javadoc_dir)/*
	$(JAVADOC) -protected -d $(javadoc_dir) \
		-classpath $(client_compile_cp) \
		-sourcepath $(src_dir):$(sable_dabl_out_dir) \
		-subpackages $(package_name) \
		-exclude $(test_package_name)
	git add $(javadoc_dir)/
	git commit -am "Generated api docs"
	git push
