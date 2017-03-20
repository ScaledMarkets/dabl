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
## Syntax

A task declaration has the following syntax:

  [`public`] `task` *name* [ [when_clause](when_clause.md) ]
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
  inputs A $thisdir/**.java of "my_repo" in my_git
  inputs XYZ
  outputs B ./**.class, ./**.txt
```
can read from A and XYZ, and write to B. When the task starts, the inputs are copied
into the temporary filesystem of the container, with a separate root level directory
for each input or output. On successful task completion, the outputs are copied to the
file system of the DABL process context. The directory structure of the inputs
and outputs is preserved, as are file attributes.

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
