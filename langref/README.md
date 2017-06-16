# DABL Language Reference, Version Alpha

## Current Status

Note: DABL version 1.0 is still being developed, and this spec reflects that
development and is therefore subject to change.
Once the spec has been labeled version 1.0, it will not change: changes to the language
will be published as separate versions. After 1.0 has been published, great effort
will be made to avoid breaking changes in future versions: we view backward compatibility
as an essential feature of a build language—indeed, of any infrastructure-as-code tool.

## Background

This is the DABL language normative spec.

The purpose of the DABL language is to provide a robust way to define highly
reliable, maintainable, and composable software build programs. DABL supports the concept
of "infrastructure as code", and as such it is designed in a manner similar
to a programming language. It is not a programming language, however: e.g.,
one cannot define loops or perform arithmetic.

## Language Elements

The top level elements of a DABL file are as follows:

* [Namespace Declaration](namespace_decl.md)
* [Import Declarations](import_decl.md)
* [Typographic Declarations](typographic_decl.md)
* [Artifact Declarations](artifact_decl.md)
* [Repo Declarations](repo_decl.md)
* [Files Declarations](files_decl.md)
* [Function Declarations](function_decl.md)
* [Task Declarations](task_decl.md)
* [Translation Declarations](translation_decl.md)

The namespace declaration must appear first, but aside from that the ordering of
these constructs is immaterial. There can be only one namespace declaration
in a DABL file.

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
type of comment. DABL does not use any statement terminators such as semi-colon
or newline, and statement indentation is ignored.

## Literal String Values

A string literal must be delimited by double quotes, and can continue across
multiple lines. For example, the following is a two-line string value that contains
one embedded line break:

<pre>
"This string value contains
two lines, and one line break."
</pre>

A string can also be delimited by triple quotes, as follows:

<pre>
"""This string value contains
two lines, and "one" line break."""
</pre>

This allows one to embed quotes within the string value.

DABL does not modify line breaks within a string, so line breaks are whatever is present
in the string value in the file - i.e., either a NL or CR/NL pair.

DABL supports static string concatenation expressions: these are string values
that are joined together during the Analysis phase
(see [Processing Phases](#processing-phases) below).
The concatenation operator
is the caret symbol: `^`. For example, the following concatenation is performed
during source analysis to produce the value `"abcde"`:
```
"ab" ^ "cde"
```

## Allowed Characters

DABL files may contain any Unicode character. However, DABL keywords are limited
to the set of [a-z] characters, and are case-sensitive; and DABL identifiers
are limited to the Unicode equivalents of the ASCII characters `a-z, A-Z, 0-9, _`,
and must begin with a letter or underscore. In practice, the only place where
characters outside this range can occur is in a file path or the name of an
external method in a function declaration.

DABL is case-sensitive.

## Environment Variables

A DABL tool may make operating system environment variables visible to the context
of the DABL file. In a DABL file, environment variables are referenced by prefixing the
name of the environment variable with a dollar sign. For example, the following
refers to an environment variable named "PATH":

<dl>
<dd><code>$PATH</code></dd>
</dl>

One can also reference an environment variable by enclosing it in curly braces,
prefixed by a dollar sign:

<dl>
<dd><code>${PATH}</code></dd>
</dl>

If an environment variable is undefined, DABL generates an error.

### Built-In Environment Variables

DABL defines some built-in environment variables that are set by the DABL
template processor at the start of execution. These are:

<dl>
<dd><code>thisdir</code> - The directory that the DABL script exists in.
Note that this can be undefined (an empty string) if the DABL script has been
transported alone (without the other contents of the directory in which
it lives) by HTTP.</dd>
<dd><code>home</code> - The home directory of the user Id that is compiling
the DABL file.</dd>
<dd><code>~</code> - Same as <code>home</code>.</dd>
</dl>

The DABL compiler also reads the following environment variable:

<dl>
<dd><code>DABL_PATH</code> - A colon-separated list of file directories in which
to look for DABL files when resolving imported namespaces.
</dl>

## Identifiers

A DABL identifier is a name that is declared within a DABL file.

### Character Set

Identifiers may consist of Unicode characters within the range

```
a-z
A-Z
0-9
_ (underscore)
```

of the Latin-1 character set, and must not begin with a number.

### Name Scope

Identifiers declared within a namespace are scoped to that namespace unless they
are declared `public`.

### Uniqueness and Name Overloading

Any identifiers declared within a namespace at the outermost level must be unique.
For example, a task may not be named the same as a repo.

A declared identifier may be identical to the namespace name; however, doing that
will mean that to reference the identifier one will have to qualify it (i.e., prefix it)
with the namespace name. For example, if the namespace is called `abcstuff`,
and within that namespace one defines a task called `abcstuff`, then to refer
to the task, one would have to use the syntax `abcstuff.abcstuff`.

## Processing Phases

A DABL file is processed in the following phases:
<ol>
<li><b>Template</b> - All environment variable references in the file are converted to
	their runtime values, regardless of the context in which they appear (e.g.,
	inside of a string). Thus, environment variable values are bound
	at the time that a DABL file is parsed. This is a simple token replacement
	and is not related to the syntax of the DABL language, much like the
	pre-processing phase in C.</li>
<li><b>Parse</b> - The output of the prior phase is parsed and an object model is created.</li>
<li><b>Analysis</b> - Identifiers are resolved, matching them up with their declarations.
	String concatenations are performed. Logical and arithmetic expressions
	<i>may</i> be partially evaluated, where possible. Values
	that depend on the DABL file context (e.g., its location on a file system)
	are elaborated.</li>
<li><b>Execution</b> - The actions implied by the file are performed. The actions depend
	on the tool that is processing the DABL file. Actions might include contextual
	analysis such as security analysis or other kinds of integrity analysis; it
	may also include preparation for execution, such as adding listeners for events,
	etc. Execution can happen right after a DABL file is parsed,
	or it can happen at any time later.</li>
</ol>

## Syntax Notation

This reference uses the following notation:

[something] - indicates that "something" is optional.

something... - indicates that "something" is a sequence of one or more things.

[something...] - indicates that "something" is a sequence of zero or more things.

*something* - indicates that "something" is a value such as an identifier,
  string, or numeric value.

`something` - indicates that "something" is verbatim.
