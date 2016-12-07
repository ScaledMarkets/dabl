# DABL Language Reference

The elements of a DABL file are as follows:

* [Namespace Declarations](namespace_decl.md)
* [Import Declarations](import_decl.md)
* [Typographic Declarations](typographic_decl.md)
* [Artifact Declarations](artifact_decl.md)
* [Repo Declarations](repo_decl.md)
* [Files Declarations](files_decl.md)
* [Function Declarations](function_decl.md)
* [Task Declarations](task_decl.md)
* [Translation Declarations](translation_decl.md)

The ordering of these constructs is immaterial. However, there can be only one
namespace declaration.

## Indentation

All DABL constructs can span multiple lines. If a DABL construct continues onto a
new line, the new line must be indented by one tab more than the line on which
the construct began,
or the equivalent number of spaces specified by the most recent tab declaration.
A multi-line construct ends at the line that returns to a lower level of indentation.

## Allowed Characters

DABL files may contain any Unicode character. However, DABL keywords are limited
to the set of [a-z] characters, and are case-sensitive; and DABL identifiers
are limited to the Unicode equivalents of the ASCII characters `a-z, A-Z, 0-9, _`,
and must begin with a letter or underscore. In practice, the only place where
characters outside this range can occur is in a file path or the name of an
external method in a function declaration.

## Environment Variables

A DABL tool may make operating system environment variables visible to the context
of the DABL file. In a DABL file, environment variables are referenced by prefixing the
name of the environment variable with a dollar sign. For example, the following
refers to an environment variable named "PATH":

<dl>
<dd><code>$PATH</code></dd>
</dl>

## Processing Phases

A DABL file is processed in the following phases:
<ol>
<li>Parse - The file is parsed and an object model is created.
<li>Elaboration - All environment variables in the object model are converted to
	their runtime values.
<li>The actions implied by the file are performed. The actions depend on the tool
	that is processing the DABL file. Actions might include adding listeners
	for events, etc.
</ol>

## Syntax Notation

This reference uses the following notation:

[something] - indicates that "something" is optional.

something... - indicates that "something" is a sequence of one or more things.

[something...] - indicates that "something" is a sequence of zero or more things.

*something* - indicates that "something" is a value such as an identifier,
  string,, or numeric value.

`something` - indicates that "something" is verbatim.
