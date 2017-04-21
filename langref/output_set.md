# Outputs Clause

## Purpose

## Syntax

<dl>
<dd><code>outputs</code> [ <i>Id</i> ] <i><a href="artifact_set.md">artifact_set</a></i></dd>
</dl>
or
<dl>
<dd><code>outputs </code> <i>Id</i> <code>new local</code> [ files-stmts ]</dd>
</dl>

The first form is just like a <code><a href="files_decl.md">files</a></code>
declaration, except that the artifacts that are declared are treated as outputs
to the task.

The second form specifies that a new local repository should be created for the task.
This is really just a directory on the host system.

If an Id is specified, it is visible outside the task, as if it had been declared
in a <code>files</code> declaration.
