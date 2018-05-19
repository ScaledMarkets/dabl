# Do not run this makefile alone. Can only be run from the main makefile.

.PHONY: all compile

all: clean compile

compile: $(common_build_dir)
	$(MVN) compile --projects test

....clean:
