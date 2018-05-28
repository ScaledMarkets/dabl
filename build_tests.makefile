# Not used anymore

.PHONY: all compile

all: clean compile

compile:
	@echo "Compiling tests-----------------------------------------------------"
	$(MVN) compile --projects test

clean:
