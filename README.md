# Dependent Artifact Build Language (DABL) <img src="duck.png" />

# Project Status

Under active development. See the [DABL Roadmap](Roadmap).

# Open Source License

See [package-info.java](java/scaledmarkets/dabl/package-info.java).

# What Is DABL?

DABL stands for "Dependent Artifact Build Language". It is a language for defining
software dependencies. This project defines the [language](langref/README.md),
and provides a robust container-based implementation—a "hermetic" build tool.

# Quick Start

Click [here](QuickStart). (Under development - the DABL tool is not yet ready
for a "quick start" - check back in a few months, when it comes out of alpha.)

# User Guide

Click [here](UserGuide). (Under development.)

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
application programming languages that are available. DABL forms the foundation
for a series of continuous delivery tools in
the [SafeHarbor](https://github.com/ScaledMarkets/SafeHarbor) tool suite.

# Technical Motivation

Existing “build” languages (e.g., make, ant, maven, gradle, Jenkins “pipeline”)
leave much to be desired. They tend to be non-[composable](https://en.wikipedia.org/wiki/Composability),
weakly typed, and have poor extensibility features. As such, they make build processes
brittle and unreliable and also limit reusability.

Dependency modeling is another problem area. Some tools (e.g., `maven`) do an
excellent job of modeling dependencies between projects, and we want to provide
that with DABL. However, dependencies within a project are another matter:
some (e.g. `gradle`) implement a dependency
model in which one defines dependencies between tasks; but what is actually needed
is a model in which a task depends on artifacts. `make` has such a model, but
`make`'s model is insufficient for today's complex package hierarchies and today's
repositories.

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
Yet, **infrastructure code definitely warrants high reliability and maintainability**,
and—increasingly—build pipelines are part of infrastructure. Thus, the time for
reliable, composable, and maintainable build languages has come.

Treating **pipeline definition as a first-class design activity**, supported by a
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
	and outputs** and **no side effects**. Google refers to this as being *hermetic*.
* **Artifact centric**: Dependencies on artifacts can be easily specified, including
	artifacts that exist in repositories, and that consist of file hierarchies.
* **Unambiguous**: The build language is **concise but not cryptic**, and encourages the definition of
	**builds that are easy to read and understand**, and that are **unambiguous**.
	Cryptic syntaxes and semantic subtleties are not appropriate for a build language,
	which is code that is not touched on a daily basis and so those who maintain that code
	often forget language nuances. Again, *ambiguity is the enemy of reliability*.
* **Static analysis**: The build language **lends itself well to static analysis**, for—say—security analysis.
	(For that to be possible, it is essential that the language is *not merely an
	extension of a general purpose language*, since a general purpose language would
	allow one to escape from the design patterns that are defined by the build
	language, making static analysis of intent much more difficult.)
* **Backward compatible**: The maintainers of the build language have a high regard for **backward
	compatibility**, so that language changes do not contribute to the instability
	of build systems. We (Scaled Markets) plan to use a deprecation approach when
	breaking changes are necessary, which we hope will be extremely infrequent; in
	addition, we plan to submit both the DABL language and the runtime object model
	(Abstract Syntax Tree) to a standards body.

# What's In This Project

* The language definition (formal grammar and
    [AST](https://en.wikipedia.org/wiki/Abstract_syntax_tree) definition file
    [dabl.sablecc](dabl.sablecc), and [Language Reference](langref/README.md)).
* A full featured reference implementation of DABL, written in Java using version 3.2 of the
	[SableCC compiler generation tool](http://www.sablecc.org/).
	The implementation binary is a Java package, which has a main method—and so it
	can be called form a command line—but it can also
	be called via its API, so that the component can be embedded in other
	Java applications.
	(Java API docs can be found [here](https://scaledmarkets.github.io/dabl/).)
* A sample DABL file, [example.dabl](example.dabl).

# Installing the Reference Implementation

Installation consists of,

* Making sure that the docker daemon is running on the host that will be executing
	the DABL tool.
* Pulling the required base container image so that it is resident in the host's
	local docker image repository. [TBD: put the image in dockerhub]
* Placing the DABL jar file (`dabl.jar`) on the host system.
* Obtaining the third party components and adding them to the classpath used
	to run DABL. See [Third Party Components](#third-party-components).
* Obtaining the unix socket native library for the host's architecture, and adding
	it to the LD_LIBRARY_PATH environment variable. We currently provide compiled
	binaries for Mac and Linux. [TBD: provide instructions.]

# Using the Reference Implementation

Command line usage is as follows:

<code>java -jar dabl.jar</code> [ options ] <i>filename</i>

where options can be

<dl>
<dd><code>-p</code> or <code>--print</code> (print the AST)</dd>
<dd><code>-t</code> or <code>--trace</code> (print stack trace instead of just error msg)</dd>
<dd><code>-a</code> or <code>--analysis</code> (analysis only - do not perform any actions)</dd>
<dd><code>-s</code> or <code>--simulate</code> (simulate only - print tasks instead of executing them)</dd>
<dd><code>-o</code> or <code>--omitstd</code> (do not implicitly import package <code>dabl.standard</code>)</dd>
<dd><code>-k</code> <i>task-name</i> or <code>--keep</code> <i>task-name</i> (keep - i.e.,
	don't delete - the specified container after execution;
	option may be specified more than once in order to keep multiple containers)</dd>
<dd><code>-h</code> or <code>--help</code></dd>
</dl>

When running DABL, the following environment variables can be set:

<dl>
<dd><code>dabl.local_repository_dir</code> - The host directory into which to store
	intermediate build output. Defaults to the current directory.</dd>
<dd><code>dabl.repo.providers.</code><i>repo-provider</i> - The Java class name of
	the repository provider for accessing repositories of type <i>repo-provider</i>.
	(See <i>repo_type</i> <a href="langref/repo_decl.md#syntax">here</a>.)</dd>
<dd><code>dabl.task_container_image_name</code> - The name of the docker image
	to use for task execution containers. Normally a user will not change this
	setting from its default, which references an image in dockerhub. However, an
	enterprise might want to replace the setting with an image maintained in an
	internal image repository. [TBD: Include reference to the image's dockerfile.]</dd>
</dl>

These values can also be set in a <code>.dabl.properties</code> file in the current
directory or the user's home directory. Enviornment settings take precedence;
after that, the current directory, and then the user's home directory. If the required
value is not found in any of those locations, then the classpath is searched for
the default <a href="https://github.com/ScaledMarkets/dabl/blob/master/.dabl.properties"><code>.dabl.properties</code></a> file.

Note: At present, the required third-party jars are not included in the DABL
jar file, `dabl.jar`.
However, in the future, they will be, making `dabl.jar` an "omnibus" (i.e.,
self-contained) jar. At present,
you need to add these to the classpath when running DABL. For the required
third party jars, see the variable `third_party_cp` in
[`makefile`](makefile).

# Setting Container (Task Runtime) Properties

DABL executes tasks in containers. Task execution often involves loading
resources. You can control resource loading via settings in a `.dabl.container.properties`
file. It may have the following settings:

<dl>
<dd><code>dabl.function_handler.<i>language</i></code> - The full name of
	the Java class that handles function calls in the specified language.
	(This setting is passed to the container runtime, since that is when
	task functions are loaded.)</dd>
</dl>

It may have additional settings that are required by the FunctionHandlers that
load classes in the container. See [Binding to a Function at Runtime](https://github.com/ScaledMarkets/dabl/blob/master/langref/func_call_stmt.md#binding-to-a-function-at-runtime).

The `dabl.container.properties` file may be in any of the places that the
`.dabl.properties` file may be. These values may not be set via environment
variable, however.

The special environment variable OmitPackageStandard is used by the reference
implementation to control whether the container processes DABL package `Standard`.

# Embedding the DABL Binary In an Application

The DABL compiler is designed as a self-contained embeddable component that can be
embedded in Java applications. For a guide on how to embed the DABL compiler,
see [Embedding the Compiler In an Application](embedding/README.md).

# Reference Implementation Design

See [java/scaledmarkets/dabl](java/scaledmarkets/dabl).

Javadocs can be found [here](https://scaledmarkets.github.io/dabl/).

# Third Party Components

The third party components that are required for building or running DABL are,

* [Jersey](https://jersey.java.net/)
* JaxRS-RI (part of [Jersey](https://jersey.github.io/) 2.0)
* A modified version of [JUnixSocket](https://github.com/kohlschutter/junixsocket).
  The modified version is obtainable from here (TBD).
* [Apache HTTP client library](https://hc.apache.org/)
* [JavaxJSON](https://jsonp.java.net/)

The additional third party tools that are required for building or testing DABL are,

* [SableCC](http://www.sablecc.org/)
* [Cucumber-JVM](https://github.com/cucumber/cucumber-jvm)

# Building the Project

To build the project, set the environment-specific tool locations in
[makefile.inc](https://github.com/Scaled-Markets/dabl/blob/master/makefile.inc).

Once you have set the third party component locations in `makefile.inc`,
run `make all`. The output of the
build is a JAR file, `dabl.jar`, which can be included in any
Java project. (See "Binary Download" below.)

Note that the Java class `scaledmarkets.dabl.Config` is generated by the build
process, so do not edit it by hand.

## Why We Do Not Use Maven or Gradle To Build the Project

We do - our makefiles call `maven`. However, `maven` does not do everything we need
to do - e.g., we need to run a compiler generation tool. In addition, the test suite
is not a unit test suite, but instead is a behavioral test suite, which does not fit
well with `maven`'s phases, because for behavioral tests, one has to deploy
before one tests. The Java-centric nature of `maven` is also an issue, because
this project requires some native (platform-specific) code.
Gradle also is not a good fit because there is no
`gradle` task for compiler generation, and so we would have had to write one, and
writing a new `gradle` task is not that simple. Indeed, these deficiencies are
part of the motivation for creating DABL. We decided that `make` is actually
the best tool for this project, because its core model is artifact-centric
rather than task-centric, and it is also language-neutral. However, we use maven
for the things that maven is good at - namely pulling the third party projects
and compiling everything. At some point we will
make DABL build itself - i.e., we will create a DABL build file for the `dabl` project.

# Binary Download

Binaries of the reference implementtaion JAR file can be found
[here](https://github.com/Scaled-Markets/dabl/releases).
Javadocs can be found [here](https://scaledmarkets.github.io/dabl/).
