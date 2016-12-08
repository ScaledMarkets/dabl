# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc


################################################################################
# Tasks

all: test dist

# Create the manifest file for the JAR.
manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME)" >> Manifest
	echo "Specification-Version: $(DABL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

# Create a Config.java file that contains the current application version.
config:
	echo "package scaledmarkets.dabl;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DablVersion = \"$(DABL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

# Generate the DABL parser.
parser: dabl.sablecc $(sable_out_dir) # Generate dabl compiler tables and classes.
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_out_dir) --no-inline dabl.sablecc
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(build_dir) -d $(build_dir) \
		$(sable_out_dir)/$(package)/node/*.java \
		$(sable_out_dir)/$(package)/lexer/*.java \
		$(sable_out_dir)/$(package)/analysis/*.java \
		$(sable_out_dir)/$(package)/parser/*.java
	cp $(sable_out_dir)/$(package)/lexer/lexer.dat $(build_dir)/$(package)/lexer
	cp $(sable_out_dir)/$(package)/parser/parser.dat $(build_dir)/$(package)/parser

# Create the directory into which the generated parser source files will be placed.
$(sable_out_dir):
	mkdir $(sable_out_dir)

# Create the directory that will contain the compiled class files.
$(build_dir):
	mkdir $(build_dir)

# Create the directory that will contain the compiled test class files.
$(test_build_dir):
	mkdir $(test_build_dir)

# Create the directory that will contain the packaged application (JAR file).
$(jar_dir):
	mkdir $(jar_dir)

# Compile the generated parser source files.
$(classfiles): $(build_dir) $(sourcefiles) config parser
	$(JAVAC) -cp $(buildcp) -d $(build_dir) \
		$(src_dir)/$(package)/*.java \
		$(src_dir)/$(package)/main/*.java \
		$(src_dir)/$(package)/gen/*.java \
		$(src_dir)/$(package)/provider/*.java

# Compile the test source files.
$(testclassfiles): $(test_build_dir) $(testsourcefiles)
	$(JAVAC) -cp $(test_compile_cp) -d $(test_build_dir) $(test_src_dir)/$(test_package)/gen/*.java
	$(JAVAC) -cp $(test_compile_cp) -d $(test_build_dir) $(test_src_dir)/$(test_package)/*.java

# Package application into a JAR file.
$(jar_dir)/$(JAR_NAME).jar: $(classfiles) manifest $(jar_dir)
	$(JAR) cvfm $(jar_dir)/$(JAR_NAME).jar Manifest -C $(third_party_unzip_dir) awssdk_config_default.json
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) com
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) models
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) org
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) mime.types
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(build_dir) scaledmarkets
	rm Manifest

# Define the 'compile' target so that we can reference it in .DEFAULT_GOAL.
compile: $(classfiles)

# Define 'dist' target so we can reference it in 'all' target.
dist: $(jar_dir)/$(JAR_NAME).jar

# Run parser to scan a sample input file. This is for checking that the parser
# can recognize the language.
check:
	$(JAVA) -classpath $(build_dir) scaledmarkets.dabl.main.Dabl --analyzeonly example.dabl

# Perform code quality scans.
runsonar:
	$(SONAR_RUNNER)

clean:
	rm -r -f $(build_dir)
	rm -r -f $(jar_dir)
	rm -r -f $(sable_out_dir)/*
	rm -f Manifest

info:
	@echo "Makefile for $(PRODUCT_NAME)"
