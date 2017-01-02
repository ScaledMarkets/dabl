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

There must be one and only one namespace declaration in a DABL file.

## Namespace Declared In Multiple Files

Multiple DABL files can
specify the same namespace: in that case, each file adds to the same namespace,
but only the public elements of each file are shared among the other files.
It is an error if there are any conflicts.
