# Import Declaration

## Purpose

An import declaration makes the public elements of the imported namespace visible
to the importing DABL file. Think of an import the way that you think of a
Java import.

## Syntax

<dl>
<dd><code>import</code> <i><a href="namespace_path.md">namespace-path</a></i></dd>
</dl>

## Mechanism By Which Import Occurs

A DABL import translates into a call to function that has the following Java
signature:
```
NameScope importNamespace(String[] path)
```
Implementations in other languages have a signature that is analogous to
this in the chosen language. For example, a C implementation would use the
signature,
```
NameScope* importNamespace(char* path[])
```

When a DABL compiler encounters an import statement, it calls the `importNamespace`
function. On return from the function, the compiler appends the returned
`NameScope` to the Namespace that is being parsed.

Circular references among namespaces are not allowed.
