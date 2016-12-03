# Makefile for DABL compiler.
# Written by Cliff Berg, Scaled Markets.

# This makefile contains no information about file structure or tool locations.
# All such configurations should be made in makefile.inc

include makefile.inc


################################################################################
# Tasks

all: dist

Manifest:
	echo "Main-Class: $(main_class)" > Manifest
	echo "Specification-Title: $(PRODUCT_NAME)" >> Manifest
	echo "Specification-Version: $(DECL_VERSION)" >> Manifest
	echo "Specification-Vendor: $(ORG)" >> Manifest
	echo "Implementation-Title: $(main_class)" >> Manifest
	echo "Implementation-Version: $(BUILD_TAG)" >> Manifest
	echo "Implementation-Vendor: $(ORG)" >> Manifest

Config:
	echo "package scaledmarkets.decl;" > $(src_dir)/$(package)/Config.java
	echo "public class Config {" >> $(src_dir)/$(package)/Config.java
	echo "public static final String DeclVersion = \"$(DECL_VERSION)\";" >> $(src_dir)/$(package)/Config.java
	echo "}" >> $(src_dir)/$(package)/Config.java

parser: decl.sablecc $(sable_out_dir) # Generate dabl compiler tables and classes.
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_out_dir) --no-inline decl.sablecc
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(build_dir) -d $(build_dir) \
		$(sable_out_dir)/$(package)/node/*.java \
		$(sable_out_dir)/$(package)/lexer/*.java \
		$(sable_out_dir)/$(package)/analysis/*.java \
		$(sable_out_dir)/$(package)/parser/*.java
	cp $(sable_out_dir)/$(package)/lexer/lexer.dat $(build_dir)/$(package)/lexer
	cp $(sable_out_dir)/$(package)/parser/parser.dat $(build_dir)/$(package)/parser

$(sable_out_dir):
	mkdir $(sable_out_dir)

$(build_dir):
	mkdir $(build_dir)

$(test_build_dir):
	mkdir $(test_build_dir)

$(jar_dir):
	mkdir $(jar_dir)

$(classfiles): $(build_dir) $(sourcefiles) Config parser
	$(JAVAC) -cp $(buildcp) -d $(build_dir) \
		$(src_dir)/$(package)/*.java \
		$(src_dir)/$(package)/main/*.java \
		$(src_dir)/$(package)/gen/*.java \
		$(src_dir)/$(package)/provider/*.java \
		$(src_dir)/scaledmarkets/aws/decl/AWS.java \
		$(src_dir)/scaledmarkets/docker/decl/Docker.java

$(testclassfiles): $(test_build_dir) $(testsourcefiles)
	$(JAVAC) -cp $(test_compile_cp) -d $(test_build_dir) $(test_src_dir)/$(test_package)/gen/*.java
	$(JAVAC) -cp $(test_compile_cp) -d $(test_build_dir) $(test_src_dir)/$(test_package)/*.java

dist: $(jar_dir)/$(JAR_NAME).jar

$(jar_dir)/$(JAR_NAME).jar: $(classfiles) Manifest $(jar_dir)
	$(JAR) cvfm $(jar_dir)/$(JAR_NAME).jar Manifest -C $(third_party_unzip_dir) awssdk_config_default.json
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) com
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) models
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) org
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(third_party_unzip_dir) mime.types
	$(JAR) uvf $(jar_dir)/$(JAR_NAME).jar -C $(build_dir) scaledmarkets
	rm Manifest
	
runsonar:
	$(SONAR_RUNNER)

clean:
	rm -r -f $(build_dir)
	rm -r -f $(jar_dir)
	rm -r -f $(sable_out_dir)/*
	rm -f Manifest

info:
	@echo "Makefile for $(PRODUCT_NAME)"

test: $(classfiles) $(testclassfiles) providers
	($(JAVA) -cp $(test_cp) org.junit.runner.JUnitCore \
		scaledmarkets.decl.test.GenTestSuite 2>&1) | tee test.log
		# The above sends stdout and stderr both to the console and the log file.

check: test.log dist
	$(JAVA) -jar $(jar_dir)/$(JAR_NAME).jar example.decl

install: dist
	$(PRE_INSTALL)     # Pre-install commands follow.
	$(POST_INSTALL)    # Post-install commands follow.
	$(NORMAL_INSTALL)  # Normal commands follow.

uninstall:
	$(PRE_UNINSTALL)     # Pre-uninstall commands follow.
	$(POST_UNINSTALL)    # Post-uninstall commands follow.
	$(NORMAL_UNINSTALL)  # Normal commands follow.

installcheck:
