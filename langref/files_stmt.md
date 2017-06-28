# Files Statement

## Syntax

<dl>
<dd><code>include</code> <i>fileset-spec</i></dd>
<dd>or</dd>
<dd><code>exclude</code> <i>fileset-spec</i></dd>
</dl>

where a *fileset-spec* is a string value expression that specifies a set of files
in the project. Wildcards may be used to match parts of a filename or directory,
according to the "globbing" rules—see [here](http://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-).

If `include` is specified, then the files of the *fileset-spec* are included
in the artifact set. If `exclude` is specified, then the files of the *fileset-spec*
are excluded from the artifact set.

