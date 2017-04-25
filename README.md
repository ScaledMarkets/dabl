# What Is DABL?

DABL stands for "Dependent Artifact Build Language". It is a language for defining
software dependencies.

# Business Motivation

Organizations seek greater flexibility by embracing “DevOps” methods, so that
they can release software capabilities more frequently and more reliably.
To achieve this, they automate every aspect of the software build, test,
and deployment process.

Unfortunately, automation does not equate to reliability or maintainability:
if the automation code is not robust, then automated processes can be as
unreliable as manual processes. In fact, software build and test processes are
notorious for being low quality, difficult to read or understand—and therefore
difficult to maintain and potentially unreliable.

If build, test, and deployment code is as important as application code, then
build, test, and deployment code needs a robust language, akin to the robust
application programming languages that are available.

# Technical Motivation

Existing “build” languages (e.g., make, ant, maven, gradle, Jenkins “pipeline”)
leave much to be desired. They tend to be non-[composable](https://en.wikipedia.org/wiki/Composability),
weakly typed, and have poor extensibility features. As such, they make build processes
brittle and unreliable and also limit reusability.

Another problem with existing build languages is that they don't promote idempotency.
That is, it is common that build systems operate on a "workspace" or current
directory, and so running a build or test suite often leaves behind intermediate artifacts.
Such artifacts can cause a build to succeed but then fail if run a second time.
Build languages do not generally promote idempotency, because they
don't define any kind of isolation or containerization concept for build tasks,
with inputs and outputs clearly defined and no side effects.

There is no reason why it needs to be this way: The current state of affairs stems from
the “guru” culture of system administration, and the still present 1980s era tradition
that build languages are quick-to-modify scripts that are maintained by sysadmin
“gurus” who are above accountability for the maintainability of their scripts.
Yet, infrastructure code definitely warrants high reliability and maintainability,
and—increasingly—build pipelines are part of infrastructure. Thus, the time for
reliable, composable, and maintainable build languages has come.

Treating pipeline definition as a first-class design activity, supported by a
*true language*, is consistent with the DevOps philosophy of treating the build
and deployment pipeline as a system to be designed, coded, and maintained. We
will note that a "true language" implies a normative language spec; and a "true
language" is not merely a set of methods on top of an existing general
purpose language as many so-called "domain-specific languages" are.

For robust infrastructure code, a better model than current practice is needed, whereby,

* **True language**: The build language is defined as a **true language**, with a normative language definition and
	well specified syntax and semantics (see [Language Reference](langref)),
	thereby avoiding ambiguity in the language itself.
	*Ambiguity is the enemy of reliability*.
* **Strongly typed**: The build language is **strongly typed**, in order to promote maintainability and reliability.
* **Composable**: The build language uses **information hiding**, **encapsulation**, and true **modularity** 
	in order to promote reuse, *extensibility, composability, and idempotency*.
* **Provides isolation**: The build language defines **isolation for tasks**, with **clearly defined inputs
	and outputs** and **no side effects**.
* **Unambiguous**: The build language is **concise but not cryptic**, and encourages the definition of
	**builds that are easy to read and understand**, and that are **unambiguous**.
	Again, *ambiguity is the enemy of reliability*.
* **Static analysis**: The build language **lends itself well to static analysis**, for—say—security analysis.
	(For that to be possible, it is essential that the language is *not merely an
	extension of a general purpose language*.)
* **Backward compatible**: The maintainers of the build language have a high regard for **backward
	compatibility**, so that language changes do not contribute to the instability
	of build systems. We (Scaled Markets) plan to use a deprecation approach when
	breaking changes are necessary, which we hope will be extremely infrequent; in
	addition, we plan to submit both the DABL language and the runtime object model
	(Abstract Syntax Tree) to a standards body.

# What's In This Project

* The language definition (formal grammar file [dabl.sablecc](dabl.sablecc), and
[Language Reference](langref/README.md)).
* A DABL compiler, written in Java using version 3.2 of the
	[SableCC compiler generation tool](http://www.sablecc.org/).
	The compiler is a Java package, which has a main method—and so it
	can be called form a command line—but it can also
	be called via its API, so that the compiler can be embedded in other
	Java applications.
	(Java API docs can be found [here](https://scaledmarkets.github.io/dabl/).)
* A sample DABL file, [example.dabl](example.dabl).

# Installing the DABL Compiler

Installation consists of,

* Making sure that the docker daemon is running on the host that will be executing
	the compiler.
* Pulling the required base container image so that it is resident in the host's
	local docker image repository. [TBD: put the image in dockerhub]
* Placing the DABL jar file (`dabl.jar`) on the host system.
* Obtaining the unix socket native library for the host's architecture, and adding
	it to the LD_LIBRARY_PATH environment variable. We currently provide compiled
	binaries for Mac and Linux. [TBD: provide instructions.]

# Using the DABL Compiler

Command line usage is as follows:

<code>java -jar dabl.jar</code> [options] <i>filename</i>

where options can be

<dl>
<dd><code>-p</code> or <code>--print</code> (print the AST)</dd>
<dd><code>-t</code> or <code>--trace</code> (print stack trace instead of just error msg)</dd>
<dd><code>-a</code> or <code>--analysis</code> (analysis only - do not perform any actions)</dd>
<dd><code>-s</code> or <code>--simulate</code> (simulate only - print tasks instead of executing them)</dd>
<dd><code>-h</code> or <code>--help</code></dd>
</dl>

When running DABL, the following environment variables can be set:

<dl>
<dd><code>dabl.local_repository_dir</code> - The host directory into which to store
	intermediate build output. Defaults to the current directory.</dd>
<dd><code>dabl.repo.providers.</code><i>repo-provider</i> - The Java class name of
	the repository provider for accessing repositories of type <i>repo-provider</i>.
	(See <i>repo_type</i> <a href="langref/repo_decl.md#syntax">here</a>.)</dd>
</dl>

These values can also be set in a <code>.dabl.properties</code> file in the current
directory or the user's home directory. Enviornment settings take precedence;
after that, the current directory, and then the user's home directory.

Note: At present, the required third-party jars are not included in the DABL
jar file, `dabl.jar`.
However, in the future, they will be, making `dabl.jar` an "omnibus" (i.e.,
self-contained) jar. At present,
you need to add these to the classpath when running DABL. For the required
third party jars, see the variable `third_party_cp` in
[`makefile`](makefile).

# Embedding the Compiler In an Application

The DABL compiler is designed as a self-contained embeddable component that can be
embedded in Java applications. For a guide on how to embed the DABL compiler,
see [Embedding the Compiler In an Application](embedding/README.md).

# Compiler Design

See [java/scaledmarkets/dabl](java/scaledmarkets/dabl).

Javadocs can be found [here](https://scaledmarkets.github.io/dabl/).

# Building the Project

To build the project, set the environment-specific tool locations in
[makefile.inc](https://github.com/Scaled-Markets/dabl/blob/master/makefile.inc).
The third party components that are required for running DABL are,

* [Jersey](https://jersey.java.net/)
* JaxRS-RI (part of Jersey 2.0)
* A modified version of JUnixSocket, obtainable from here (TBD)
* [Apache HTTP client library](https://hc.apache.org/)
* [JavaxJSON](https://jsonp.java.net/)

The third party tools that are required for building or testing DABL are,

* [SableCC](http://www.sablecc.org/)
* [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm)

Once you have set the third party component locations in `makefile.inc`,
run `make all`. The output of the
build is a JAR file, `dabl.jar`, which can be included in any
Java project. (See "Binary Download" below.)

Note that the Java class `scaledmarkets.dabl.Config` is generated by the build
process, so do not edit it by hand.

## Why We Did Not Use Maven or Gradle To Build the Project

Building this project entails more than compilation and packaging in a JAR:
we need to run a compiler generation tool. In addition, the test suite is not
a unit test suite, but instead is a behavioral test suite, which does not fit
well with `maven`'s phases, because for behavioral tests, one has to deploy
before one tests. The Java-centric nature of `maven` is also an issue, because
this project requires some native (platform-specific) code.
Gradle also is not a good fit because there is no
`gradle` task for compiler generation, and so we would have had to write one, and
writing a new `gradle` task is not that simple. We decided that `make` is actually
the best tool for this project, although at some point we will make it build
itself - i.e., we will create a DABL build file for the `dabl` project.

# Binary Download

Binaries of the compiler JAR file can be found [here](https://github.com/Scaled-Markets/dabl/releases).
Javadocs can be found [here](https://scaledmarkets.github.io/dabl/).

