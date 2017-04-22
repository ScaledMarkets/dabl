# Inputs Clause

## Purpose

## Syntax

<dl>
<dd><code>inputs [</code> <i>Id</i> <code>]</code> <i><a href="artifact_set.md">artifact_set</a></i></dd>
</dl>
or
<dl>
<dd><code>inputs </code> <i>task-Id . task-output-Id</i> [ <i>files-stmts</i> ]</dd>
</dl>

The first form is just like a <code><a href="files_decl.md">files</a></code>
declaration, except that the artifacts that are declared are treated as inputs
to the task. If an Id is specified, it is visible outside the task, as if it had been declared
in a <code>files</code> declaration.

The second form specifies an output that is declared elsewhere inside of a task.

When a local repository is specifed by a task input, the
repository files can be accessed from task [procedural statements](procedural_stmt.md)
by the path,

<code>/</code><i>task-output-Id</i>

See [Outputs Clause, Local Repositories](output_set.md#local-repositories) for more
information on the creation and lifecycle of local repositories.
