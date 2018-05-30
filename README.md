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
is a model in which *artifacts* depend on a task, and also tasks can depend
on other artifacts (the task's inputs). The direct task-to-task dependency
approach makes it difficult to avoid executing every task every time you run
the build. `make` has an artifact oriented model, but
`make`'s model is insufficient for today's complex package hierarchies and today's
repositories.

Maven is interesting because while it models project level dependencies well, it tends
to be complicated to use if one is doing anything out of the ordinary. This is partly
because maven has a very complex execution model - the "maven lifecycle" - and so one
has to consider which "phase" a task ("goal") should execute it. If one searches online
for maven troubles, a very common one is "my maven task should execute, but it doesn't".
A build tool should not add ambiguity or complexity to a project - it should make
things simpler. Maven also has a habit of generating a huge amount of output, 
pulling hundreds of artifacts when you run it, so that one has a feeling of not 
understandind what is
happening or being in control of the build. This is because maven's dependency
mechanism is working; but it would be more user friendly if it simply listed each
project that it pulls, instead of the enormously verbose output that it generates.
From a reliability perspective, a build process should do exactly what you expect:
it should not try to be "smart" - it should be obvious and straightforward.
Uncertainty is the enemy of reliability.

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
* A full featured reference implementation of DABL, written in Java using version 3 of the
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
* Pulling the container image scaledmarkets/taskruntime from dockerhub so that it is
	resident in the host's local docker image repository.
* Placing the DABL jar files `parser.jar`, `common.jar`, and `client.jar` on the host system.
	These can be obtained from [Maven Central](http://repo1.maven.org/maven2)
	in the scaledmarkets group Id.
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

# Project Build Structure

The main maven pom file defines five sub-projects (modules):

* parser - this generates the source code for the DABL parser, using the grammar
	specification in dabl.sablecc.
* common - this module is used by both the client and the task_runtime.
* client - this is the command line client - it has a main method.
* task_runtime - this is what executes each DABL task in a separate container; it
	has a main method as well.
* test - this is the suite of behavioral tests for DABL.

# Third Party Components

The third party components that are required for building or running the DABL client are
listed in the pom files for the sub-projects `parser`, `common`, `client`, `task_runtime`,
and `test`. Note that test is a separate sub-project, since it is an integration test.

# Building the Project

To build the project, edit JDK locations in env.mac or env.linux, and run `make all`.

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

# Javadocs

Javadocs can be found [here](https://scaledmarkets.github.io/dabl/).
