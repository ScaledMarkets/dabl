# Task Declaration

## Purpose

A task declaration is a named process to be performed when specified conditions
are met. The process is performed in complete isolation from other processes
that are defined in the DABL file, and no environment inheritance exists from
the DABL environment or from one task to another. A typical implementation of
a task is a Linux container.

The conditions that trigger execution are specified by the task’s when clause.
If there is no when clause, then the following when clause is assumed:
```
when inputs are newer than outputs
```

If the task is declared to be `open`, then the task is able to access the networks
that the host user account can access. Otherwise, the task cannot access any
networks. By default, a task is not open.

## Syntax

A task declaration has the following syntax:

  [`public`] [`open`] `task` *name* [ [when_clause](when_clause.md) ]
  	[ [input_set](input_set.md)... ] [ [output_set](output_set.md)... ]
  	[ [procedural_stmts](procedural_stmt.md)...]

### Inputs and Outputs

The inputs are the files that are read by the task, and the outputs are the files
that are created or updated by the task. Inputs and outputs can be given names,
but this is not required. Inputs and outputs *may* be copied to a temporary
workspace for task execution, rather than used in place.

### Procedural Statements

The procedural statements that are declared for a task constitute the logical process that
is defined by the task. The procedural statements are executed in the order in which
they appear. A task's procedural statements are executed in a container, and any files
created by the task are not visible to other tasks unless the files are declared
in an `output` construct.

## Example

```
task compileit
    when inputs A newer than outputs
    inputs A "$thisdir/**.java" of "my_repo" in my_git, XYZ
    outputs "./**.class", "./**.txt"
    bash "
        javac $thisdir/*.java
        "
```

In this example,

* A task named `compileit` is defined.
* An input set called `A` is defined.
* Another input set called `XYZ` is defined.
* An output set (with no name) is defined.
* The task will be invoked whenever at least one file of input set `A` has a more recent
timestamp than all of the files that are specified by the output set.
* When the task is invoked, the `bash` command will be performed, after the value
of `$thisdir` has been substituted. (`$thisdir` evaluates to the directory in which
the DABL script exists.)

## Name Scope and Name Overloading

If the input_set or output_set defines a name for the inputs or outputs,
respectively, the name is scoped to the enclosing task. However, a task
may reference the inputs or outputs of another task, by explicitly qualifying
the input or output name with the task in which it was defined.
This allows a task's input statement to reference the outputs of another task, by
qualifying the output with the task name. For example,
```
task compileit
    outputs A "$thisdir/**.java" of "my_repo" in my_git, XYZ

task packageit
    inputs compileit.A
```

An input_set or output_set may be the same as the task's name. However, if that
is the case, then to reference the task namefrom within itself, one would have to
fully qualify the task name with the namespace. For example, if a namespace
is called `abcnamespace`, and it contains a task called `somestuff`, and
that task contains an input_set called `somestuff`, then to refer to the
task, one would have to use the syntax `abcnamespace.somestuff`. A simple
reference to `somestuff` from within the task would reference the input_set.

## Semantics of the When Clause

A task's `when` clause is evaluated each time a DABL file is processed.
Thus, a non-sensical `when` clause such as,
```
    when outputs A newer than inputs
```
in which the outputs must be newer than the inputs, will cause the task to
be performed all the time.

The `inputs` and `output` clauses of a task define a dataflow in terms of the
`files` that they reference. That dataflow determines the sequence for the
evaluation of task `when` clauses. Thus, if a task's outputs are inputs to
another task, the first task's `when` clause is evaluated before the second task's.
Cycles such that a task's `outputs` feed back to its `inputs`, either directly or
indirectly, are not allowed, and a DABL file that contains that type of cycle
should compile with an error.

If a `task` has multiple `when` clauses, then the task is executed if any of them
is true.

## Task Execution

Task execution consists of performing the task’s procedural statements in sequence.
Each task creates an operating system container. (e.g., a Linux container), in which
it performs the task’s procedural statements. Task inputs and outputs are available
via input and output streams named after the input and output names. For example,
the following task,
```
task compileit
  inputs A of "my_repo" in my_git include "/**.java"
  inputs XYZ
  outputs B of "my_project" in my_artifact_repo include "./**.class", include "./**.txt"
```
can read from A and XYZ, and write to B. When the task starts, the inputs are copied
into the temporary filesystem of the container. On successful task completion,
the outputs are copied to the file system of the DABL process context.
The directory structure of the inputs and outputs is preserved, as are file attributes.

### Idempotency, Isolation, and Hermeticity

This copying and the use of a container for task execution provides a very high
level of isolation and idempotency for each task. Tasks are therefore
*hermetic*—only defined inputs and outputs may enter and exit a task, respectively.

There are qualifications to task hermeticity, however. One is that the task may be
declared to be `open`, in which case the task is able to access the networks of the
host—and therefore any resources that the host may access, including the Internet.
That breaks the hermeticity, but in a declared manner (tasks are not `open` by default),
and is necessary for tasks that need to install build tools. The installation of
a build tool is usually idempotent. It is highly advisable
that any resources obtained by an `open` task should be declared by comments for the task.
Note that DABL enables one to access external resources—including executables—via
the `files` mechanism. That mechanism copies those resources into the execution
context of the task. A future version of DABL might expand on this, enabling a
task to use the container's native package manager and possibly non-native package management
systems such as `npm` and `maven`, reducing the need for open tasks.

Another way that hermeticity is broken is that the underlying operating system
on which a task executes may be accessed by the task. Tasks execute in containers,
and so the underlying operating system is the container’s kernel and base
operating system, provided by the container base image. The container base image
is specified by the `dabl.task_container_image_name` property, and it defaults to
an Alpine image that has the latest version of Java installed. A fully documented
build process therefore consists of the DABL file and the Dockerfile for the
images that are used to execute tasks.

Changes made to a
container's file system are discarded after the task exits, but the container file system is
still a path for data to enter the task.

### Task Lifecycle Model

A task is only executed if its `when` condition is met.

If an error occurs while performing a task procedural statement, the default behavior
is to terminate the task. However, an error handler can be installed using the `on` keyword.
Once an error handler is installed, the error handler is triggered if the task
performs a procedural statement that returns a non-zero process status. A
procedural statement can obtain the error status via the built-in function `error_status()`.
When an error handler is triggered, it resets the error status before completing.
If a task generates an error but the error is handled, then the task is considered
to have succeeded.
