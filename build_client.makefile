

# Uses variables:
#	JAVAC, maxerrs, buildcp, third_party_cp, build_dir, src_dir, package, CurDir,
#	jar_dir, main_class, PRODUCT_NAME, DABL_VERSION, ORG, BUILD_TAG, JAR_NAME,
#	classfiles, JAR, javadoc_dir, JAVADOC, src_dir, sable_dabl_out_dir, package_name

all: clean jar javadoc

.PHONY: compile clean manifest javadoc

# Intermediate artifacts:
export classfiles := \
	$(build_dir)/$(package)/*.class \
	$(build_dir)/$(package)/docker/*.class \
	$(build_dir)/$(package)/exec/*.class \
	$(build_dir)/$(package)/helper/*.class \
	$(build_dir)/$(package)/analysis/*.class \
	$(build_dir)/$(package)/analyzer/*.class \
	$(build_dir)/$(package)/util/*.class \
	$(build_dir)/$(package)/lexer/*.class \
	$(build_dir)/$(package)/node/*.class \
	$(build_dir)/$(package)/repos/*.class \
	$(build_dir)/$(package)/handlers/*.class \
	$(build_dir)/$(package)/parser/*.class

# 
compile: 
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp):$(third_party_cp) -d $(build_dir) \
		$(src_dir)/sablecc/*.java \
		$(src_dir)/$(package)/*.java \
		$(src_dir)/$(package)/docker/*.java \
		$(src_dir)/$(package)/analyzer/*.java \
		$(src_dir)/$(package)/exec/*.java \
		$(src_dir)/$(package)/util/*.java \
		$(src_dir)/$(package)/repos/*.java \
		$(src_dir)/$(package)/helper/*.java
	cp $(CurDir)/.dabl.properties $(build_dir)

clean:
	if [ -z "$(build_dir)" ]; then echo "ERROR: build_dir is empty"; exit 1; fi
	rm -r -f $(build_dir)/*
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir is empty"; exit 1; fi
	rm -r -f $(jar_dir)/*

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
	$(JAR) cvfm $(jar_dir)/$(JAR_NAME).jar Manifest -C $(build_dir) scaledmarkets \
		-C $(build_dir) .dabl.properties
	rm Manifest

jar: $(jar_dir)/$(JAR_NAME).jar

# Create the directory that will contain the javadocs.
$(javadoc_dir):
	mkdir $(javadoc_dir)

# Generate API docs (javadocs).
javadoc: $(javadoc_dir)
	rm -rf $(javadoc_dir)/*
	$(JAVADOC) -protected -d $(javadoc_dir) \
		-classpath $(build_dir) \
		-sourcepath $(src_dir):$(sable_dabl_out_dir) \
		-subpackages $(package_name) \
		-exclude $(test_package_name)
	git add $(javadoc_dir)/
	git commit -am "Generated api docs"
	git push
