# Files Declaration

## Purpose

A files declaration identifies a set of files, obtainable from a named repo via a given path.
A files declaration can specify a particular repo version, or state that the most
recent versions should be used.

# Syntax

A files declaration has the following syntax:

`files` *Id* `from` *repo-Id* `in` *project-Id* *files_stmts*

The *Id* is used to refer to the collection of files elsewhere in the DABL file.
The *repo-Id* is the Id of a repository that is defined by a repo declaration.
The *project-Id* is the name of a project (e.g., a github "repo") within the
repository.

*files-stmts* consists of zero or more `prefer` statements. The syntax of a
`prefer` statement is as follows:

`prefer` *preference*

where *preference* is one of,

latest version_criteria

or

version_spec

and version-criteria is one of,

compatible with
tested with





