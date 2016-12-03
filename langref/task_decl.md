# Task Declaration
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
## Structure Of a Task Declaration

A task declaration has the following syntax:

  [`public`] `task` *name* [task_stmts...]

A task_stmt can be one of,

* [A when statement](when_stmt.md)
* [An inputs statement](inputs_stmt.md)
* [An outputs statement](outputs_stmt.md)
* [A procedural statement](procedural_stmt.md)


## Example

```
task compileit
    when inputs A newer than outputs
    inputs A $thisdir/**.java from "my_repo" in my_git
    inputs XYZ
    outputs ./**.class, ./**.txt
    bash "
        javac $thisdir/*.java
```

In this example,

* A task named "compileit" is defined.
* An input set called "A" is defined.
* Another input set called "XYZ" is defined.
* An output set (with no name) is defined.
* The task will be invoked whenever at least one file of input set A has a more recent
timestamp than all of the files that are specified by the output set.
* When the task is invoked, the bash command will be performed, after the value
of $thisdir has been substituted. ($thisdir evaluates to the directory in which
the script exists.)
