# Do not run this makefile alone. Can only be run from the main makefile.

# Uses variables:
#	jar_dir, JAR_NAME, sable_out_dir, package, JAVA, sable, grammar_file, JAVAC,
#	maxerrs, buildcp, parser_build_dir

all: clean compile

.PHONY: dist jar gen compile clean


# Define 'dist' target so we can reference it in 'all' target.
dist: jar

# Create 'jar' target so we can reference it in 'all' target.
jar: $(jar_dir)/$(JAR_NAME).jar

# Create the directory that will contain the parser source files.
$(sable_out_dir):
	mkdir -p $(sable_out_dir)/$(package)

# Create the directory that will contain the parser's compiled class files.
$(parser_build_dir):
	mkdir -p $(parser_build_dir)

# Generate the task parser.
# Generates task compiler tables and classes.
gen: $(grammar_file) $(sable_out_dir)
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_out_dir) --no-inline $(grammar_file)

# Compile the generated code for the task parser.
compile: gen
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp) -d $(parser_build_dir) \
		$(sable_out_dir)/$(package)/node/*.java \
		$(sable_out_dir)/$(package)/lexer/*.java \
		$(sable_out_dir)/$(package)/analysis/*.java \
		$(sable_out_dir)/$(package)/parser/*.java
	cp $(sable_out_dir)/$(package)/lexer/lexer.dat $(parser_build_dir)/$(package)/lexer
	cp $(sable_out_dir)/$(package)/parser/parser.dat $(parser_build_dir)/$(package)/parser

clean:
	if [ -z "$(sable_out_dir)" ]; then echo "ERROR: sable_out_dir variable is not set"; exit 1; else rm -r -f $(sable_out_dir)/*; fi
	if [ -z "$(parser_build_dir)" ]; then echo "ERROR: parser_build_dir variable is not set"; exit 1; else rm -r -f $(parser_build_dir)/*; fi
