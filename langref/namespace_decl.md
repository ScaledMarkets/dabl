# Namespace Declaration

## Purpose

A namespace declaration defines the a container for the contents of the file.
Thus, other DABL files (or tools that process DABL) can refer to elements of the
file via the namespace. Think of a namespace the way that you think of a Java package.

## Syntax

A namespace declaration has the following syntax:

<dl>
<dd><code>namespace</code> <i><a href="namespace_path.md">namespace-path</a></i>
</dl>

The namespace-path is the name
that is assigned to the namespace. For example, the following declares a
namespace called `mycompany.myproject`:

```
namespace mycompany.myproject
```

The identifiers that make up a namespace
must consist of Unicode characters within the range

```
a-z
A-Z
0-9
_ (underscore)
- (hyphen)
```

of the Latin-1 character set, and must
not begin with a number.

There must be one and only one namespace declaration in a DABL file.

Multiple DABL files can
specify the same namespace: in that case, the files are logically combined. It is
an error if there are any conflicts when combining.
