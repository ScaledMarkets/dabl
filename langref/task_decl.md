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

[`public`] `task` *name*

[when_clause]

[inputs_clause]

[outputs_clause]

task_stmts...

A task declaration may contain these elements, in this order:

1. When clause
2. Inputs clause
3. Outputs clause
4. Procedural statements

