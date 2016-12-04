# Files Declaration

## Purpose

A files declaration identifies a set of files, obtainable from a named repo via a given path.
A files declaration can specify a particular repo version, or state that the most
recent versions should be used.

# Syntax

A files declaration has the following syntax:

<dl>
<dd><code>files</code> <i>Id</i> <code>from</code> <i>repo-Id</i> <code>in</code>
<i>project-Id files-stmts</i>
</dl>

The *Id* is used to refer to the collection of files elsewhere in the DABL file.
The *repo-Id* is the Id of a repository that is defined by a repo declaration.
The *project-Id* is the name of a project (e.g., a github "repo") within the
repository.

*files-stmts* consists of zero or more `prefer` statements. The syntax of a
`prefer` statement is as follows:

<dl>
<dd><code>prefer</code> <i>preference</i>
</dl>

where *preference* is one of,

<dl>
<dd><code>latest</code> <i>version-criteria</i></dd>
<dd><i><a href="version-spec.md">version_spec</a></i></dd>
</dl>

and version-criteria is one of,

<dl>
<dd><code>compatible with</code></dd>
<dd><code>tested with</code></dd>
</dl>






