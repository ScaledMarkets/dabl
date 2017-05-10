# Artifact Set

## Purpose

An artifact set identifies a set of files that exist somewhere that DABL knows
how to find them: that is, in a declared [repo](repo_decl.md), or in the workspace
of the task specifying the artifact set.

## Syntax

An artifact set has the following syntax:

<dl>
<dd><code>of</code> <i>project-Id</i> <code>in</code> <i>repository-Id</i> [ <i>prefer-stmt</i> ] [ <i>files-stmts</i> ]</dd>
</dl>

The *repository-Id* is the Id of a repository that is defined by
a [repo declaration](repo_decl.md).

The *project-Id* is the name of a project (e.g., a github "repo") within the
repository.

*files-stmts* have the following syntax:

<dl>
<dd><code>include</code> <i>fileset-spec</i></dd>
<dd></dd>
<dd><code>exclude</code> <i>fileset-spec</i></dd>
</dl>

where a *fileset-spec* is a string value expression that specifies a set of files
in the project. Wildcards may be used to match parts of a filename or directory.

If `include` is specified, then the files of the *fileset-spec* are included
in the artifact set. If `exclude` is specified, then the files of the *fileset-spec*
are excluded from the artifact set.

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
