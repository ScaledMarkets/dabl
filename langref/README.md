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

## Comments

DABL has four types of comment:
<ol>
<li>Single line comment: all text after <code>//</code> through the end of the line is
	treated as a comment.</li>
<li>Single line syntactic comment: Same as single line comment, except that the
	comment begins with <code>///</code>. In addition, the comment is syntactically associated
	with the DABL construct immediately prior to the comment. This is useful for
	documentation generation tools.</li>
<li>Multi-line comments: all text between <code>/*</code> and <code>*/</code>, possibly spanning
	multiple lines, is treated as a comment.</li>
<li>Multi-line syntactic comment: Same as a multi-line comment, but the delimiters are
	<code>//*</code> and <code>*//</code> to begin and end the comment, respectively.
	In addition, the comment is synactically associated with the DABL construct
	that immediately follows the comment. This is useful for documentation generation tools.</li>
</ol>

## Line Continuation

All DABL constructs can span multiple lines. Only multi-line comments must be
terminated with either a <code> */</code> or <code> *//</code>, depending on the
type of comment.

## Strings

A DABL string value does not need to be quoted unless it contains embedded whitespace.
A string can continue across multiple lines if it is delimited by three double-quotes (""").
For example, the following is a multiline string value:

<pre>
"""
This string value contains
two lines.
"""
</pre>

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
<li>Template - All environment variables in the file are converted to
	their runtime values.</li>
<li>Parse - The output of the prior phase is parsed and an object model is created.</li>
<li>Elaboration - Expressions are evaluated where
	they appear; if an unquoted string expression evaluates to a variable reference, then
	the variable's value is used instead of the string. Note also that variables
	are only defined in the return value of a function call. Prepositions that
	are defined in function declarations are also recognized during this phase.</li>
<li>Execution - The actions implied by the file are performed. The actions depend on the tool
	that is processing the DABL file. Actions might include adding listeners
	for events, etc.</li>
</ol>

## Syntax Notation

This reference uses the following notation:

[something] - indicates that "something" is optional.

something... - indicates that "something" is a sequence of one or more things.

[something...] - indicates that "something" is a sequence of zero or more things.

*something* - indicates that "something" is a value such as an identifier,
  string,, or numeric value.

`something` - indicates that "something" is verbatim.
