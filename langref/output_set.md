# Outputs Clause

## Purpose

## Syntax

<dl>
<dd><code>outputs [</code> <i>Id</i> <code>]</code> <i><a href="artifact_set.md">artifact_set</a></i></dd>
</dl>
or
<dl>
<dd><code>outputs </code> <i>Id</i> <code>new local</code></dd>
</dl>

If an input_set is specified, it must have at least one input.

An <code>outputs</code> clause is just like a <code><a href="files_decl.md">files</a></code>
declaration, except that the artifacts that are declared are treated as outputs
to the task.

If an Id is specified, it is visible outside the task, as if it had been declared
in a <code>files</code> declaration.
