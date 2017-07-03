# Artifact Set

## Purpose

An artifact set identifies a set of files that exist somewhere that DABL knows
how to find them: that is, in a declared [repo](repo_decl.md), or in the workspace
of the task specifying the artifact set.

## Syntax

An artifact set has the following syntax:

<dl>
<dd><code>of</code> <i>project-Id</i> <code>in</code> <i>repository-Id</i> [
	<i>prefer-stmt</i> ] [ <a href="files_stmt.md"><i>files-stmt...</i></a> ]</dd>
</dl>

The *repository-Id* is the Id of a repository that is defined by
a [repo declaration](repo_decl.md).

The *project-Id* is a string value, and is the name of a project (e.g., a github "repo")
within the repository.

*prefer-stmts* serve to specify that if the repository supports DABL's concept of
artifact versions, that the specified version criteria should be applied.

*prefer-stmts* has the following syntax:

<dl>
<dd><code>prefer</code> <i>preference</i>
</dl>

where *preference* is one of,

<dl>
<dd><code>latest</code> <i>version-criteria</i></dd>
<dd><i><a href="version_spec.md">version-spec</a></i></dd>
</dl>

and *version-criteria* is one of,

<dl>
<dd><code>compatible with</code></dd>
<dd><code>tested with</code></dd>
</dl>
