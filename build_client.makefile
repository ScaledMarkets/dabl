# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	JAVAC, maxerrs, buildcp, third_party_cp, parser_build_dir, client_build_dir,
#	src_dir, package, CurDir,
#	jar_dir, main_class, PRODUCT_NAME, DABL_VERSION, ORG, BUILD_TAG, JAR_NAME,
#	classfiles, JAR, javadoc_dir, JAVADOC, src_dir, sable_dabl_out_dir, package_name

all: clean jar javadoc

.PHONY: compile clean manifest javadoc

# Intermediate artifacts:
export classfiles := \
	$(client_build_dir)/$(package)/*.class \
	$(client_build_dir)/$(package)/docker/*.class \
	$(client_build_dir)/$(package)/exec/*.class \
	$(client_build_dir)/$(package)/helper/*.class \
	$(client_build_dir)/$(package)/analysis/*.class \
	$(client_build_dir)/$(package)/analyzer/*.class \
	$(client_build_dir)/$(package)/util/*.class \
	$(client_build_dir)/$(package)/lexer/*.class \
	$(client_build_dir)/$(package)/node/*.class \
	$(client_build_dir)/$(package)/repos/*.class \
	$(client_build_dir)/$(package)/handlers/*.class \
	$(client_build_dir)/$(package)/parser/*.class

# 
compile: 
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp):$(third_party_cp) -d $(client_build_dir) \
		$(src_dir)/$(package)/*.java \
		$(src_dir)/$(package)/docker/*.java \
		$(src_dir)/$(package)/analyzer/*.java \
		$(src_dir)/$(package)/exec/*.java \
		$(src_dir)/$(package)/util/*.java \
		$(src_dir)/$(package)/repos/*.java \
		$(src_dir)/$(package)/helper/*.java
	cp $(CurDir)/.dabl.properties $(client_build_dir)

clean:
	if [ -z "$(client_build_dir)" ]; then echo "ERROR: client_build_dir variable is not set"; exit 1; fi
	rm -r -f $(client_build_dir)/*
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(JAR_NAME)" ]; then echo "ERROR: JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(JAR_NAME).jar

# Create the manifest file for the JAR.
manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME)" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

$(jar_dir)/$(JAR_NAME).jar: manifest compile
	$(JAR) cvfm $(jar_dir)/$(JAR_NAME).jar Manifest .dabl.properties \
		-C $(client_build_dir) scaledmarkets \
		-C $(parser_build_dir) scaledmarkets \
	rm Manifest

jar: $(jar_dir)/$(JAR_NAME).jar

# Create the directory that will contain the javadocs.
$(javadoc_dir):
	mkdir $(javadoc_dir)

# Generate API docs (javadocs).
javadoc: $(javadoc_dir)
	rm -rf $(javadoc_dir)/*
	$(JAVADOC) -protected -d $(javadoc_dir) \
		-classpath $(buildcp) \
		-sourcepath $(src_dir):$(sable_dabl_out_dir) \
		-subpackages $(package_name) \
		-exclude $(test_package_name)
	git add $(javadoc_dir)/
	git commit -am "Generated api docs"
	git push
