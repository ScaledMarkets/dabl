# Files Declaration

## Purpose

A files declaration identifies a set of files, obtainable from a named repo via a given path.
A files declaration can specify a particular repo version, or state that the most
recent versions should be used.

## Syntax

A files declaration has the following syntax:

<dl>
<dd>[<code>public</code>] <code>files</code> <i>Id</i> <i>artifact-set</i>
</dl>

The *Id* is used to refer to the collection of files elsewhere in the DABL file.

An <i><a href="artifact_set.md">artifact-set</a></i> specifies a set of files from somewhere.

## Example

```
files Stuff of "myrepo" in my_maven
  "*.java"
  exclude "*.class"
repo my_maven type "maven"
  path "mymaven.abc.com"
  userid "MavenUserId" password "MavenPassword"
```
