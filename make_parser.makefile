

all: clean compile

.PHONY: dist jar gen compile clean


# Define 'dist' target so we can reference it in 'all' target.
dist: jar

# Create 'jar' target so we can reference it in 'all' target.
jar: $(jar_dir)/$(JAR_NAME).jar


# Create the directory that will contain the parser source files.
$(sable_out_dir):
	mkdir -p $(sable_out_dir)/$(package)

# Create the directory that will contain the packaged application (JAR file).
$(jar_dir):
	mkdir $(jar_dir)

# Generate the task parser.
# Generates task compiler tables and classes.
gen: $(grammar_file) $(sable_out_dir) $(build_dir)
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_out_dir) --no-inline $(grammar_file)

# Compile the generated code for the task parser.
compile: gen
	$(JAVAC) -Xmaxerrs $(maxerrs) -cp $(buildcp) -d $(build_dir) \
		$(sable_out_dir)/$(package)/node/*.java \
		$(sable_out_dir)/$(package)/lexer/*.java \
		$(sable_out_dir)/$(package)/analysis/*.java \
		$(sable_out_dir)/$(package)/parser/*.java
	cp $(sable_out_dir)/$(package)/lexer/lexer.dat $(build_dir)/$(package)/lexer
	cp $(sable_out_dir)/$(package)/parser/parser.dat $(build_dir)/$(package)/parser

clean:
	rm -r -f $(sable_out_dir)/*
