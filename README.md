# What Is DABL?

DABL stands for "Dependent Artifact Build Language". It is a language for defining
software dependencies.

# Motivation

Existing “build” languages (e.g., make, ant, maven, gradle, Jenkins “pipeline”) leave much to be desired. They tend to be non-composable, weakly typed, and have poor extensibility features. As such, they make build processes brittle and unreliable and also limit reusability.

There is no reason why it needs to be this way: The current state of affairs stems from the “guru” culture of system administration, and the still present 1980s era tradition that build languages are quick-to-modify scripts that are maintained by sysadmin “gurus” who are above accountability for the maintainability of their scripts. Yet, infrastructure code definitely warrants high reliability and maintainability, and increasingly build pipelines are part of infrastructure. Thus, the time for reliable and composable build languages has come.

Treating pipeline definition as a first-class design activity, supported by a true language, is consistent with the DevOps philosophy of treating the build and deployment pipeline as a system to be designed, coded, and maintained.

A better model than current practice is needed, whereby,

* The build language is strongly typed, in order to promote maintainability and reliability.
* The build language uses information hiding, encapsulation, and true modularity in order to promote reuse and extensibility.

See also https://drive.google.com/open?id=1xoyDMebGHedfBUFcsMUkjQJHSwrt3sCFF8CDhrVaTjo

# What's In This Project

* The language definition (formal grammar file [dabl.sablecc](dabl.sablecc), and
[Language Reference](langref/README.md)).
* DABL compiler, written in Java using version 3.2 of the
	[SableCC compiler generation tool](http://www.sablecc.org/).
	The compiler is a Java package, which has a main method, but it can also
	be called via its API, so that the compiler can be embedded in other
	Java applications.

To create the compiler, run the makefile in the root directory. The output of the
build is a JAR file, dabl.jar, which can be included in any
Java project. For an example of embedding the compiler in another project,
see [TBD]().

The output of the compiler is a Java object model. The object model is defined
by the Abstract Syntax Tree at the end of the [dabl.sablecc](dabl.sablecc)
grammar file. The object
model can be accessed at runtime; thus, one can build a tool that compiles DABL
files and then operates on the resulting object model. For an example
of accessing the object model, see [TBD]().

