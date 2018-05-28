# Not used anymore

.PHONY: all gen compile clean jar

all: compile

# Generate the task parser.
# Generates task compiler tables and classes.
gen: $(grammar_file)
	$(JAVA) -jar $(sable)/lib/sablecc.jar -d $(sable_dabl_out_dir) --no-inline $(grammar_file)

# Compile the generated code for the task parser.
compile: #gen
	@echo "Compiling parser-----------------------------------------------------"
	#$(MVN) clean --projects parser
	#$(MVN) generate-resources --projects parser
	#$(MVN) process-resources --projects parser
	$(MVN) compile --projects parser
	#$(MVN) clean exec:java org.codehaus.mojo:build-helper-maven-plugin:add-source compile --projects parser
	#$(MVN) clean exec:java org.codehaus.mojo:build-helper-maven-plugin:add-source compile --projects parser
	#$(MVN) clean exec:java compile --projects parser

clean:
	$(MVN) --projects parser clean
	if [ -z "$(sable_dabl_out_dir)" ]; then echo "ERROR: sable_dabl_out_dir variable is not set"; exit 1; else rm -r -f $(sable_dabl_out_dir)/$(GroupId); fi
	if [ -z "$(jar_dir)" ]; then echo "ERROR: jar_dir variable is not set"; exit 1; fi
	if [ -z "$(PARSER_JAR_NAME)" ]; then echo "ERROR: PARSER_JAR_NAME variable is not set"; exit 1; fi
	rm -r -f $(jar_dir)/$(PARSER_JAR_NAME).jar

# Create 'jar' target so we can reference it in 'all' target.
#jar: $(jar_dir)/$(PARSER_JAR_NAME).jar

