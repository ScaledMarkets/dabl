# Do not run this makefile alone. Can only be run from the main makefile.

.PHONY: all compile

all: clean compile

compile:
	@echo "Compiling tests-----------------------------------------------------"
	$(MVN) compile --projects test

clean:
