

all: clean jar javadoc

.PHONY: compile clean manifest javadoc


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
	rm -r -f $(build_dir)/*
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

$(jar_dir)/$(JAR_NAME).jar: $(classfiles) manifest $(jar_dir) compile
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
