# Embedding the Compiler In an Application

When the compiler processes a DABL source file, the output of the compiler is a
[`CompilerState`](https://scaledmarkets.github.io/dabl/scaledmarkets/dabl/main/CompilerState.html)
object. A `CompilerState` contains a a field,
```
public Start ast;
```
A [`Start`](https://scaledmarkets.github.io/dabl/scaledmarkets/dabl/node/Start.html)
is the root of a node tree that implements the Abstract Syntax Tree (AST)
that is defined at the end of the [dabl.sablecc](dabl.sablecc)
grammar file. The AST
can be accessed at runtime; thus, one can build a tool that compiles DABL
files and then operates on the resulting AST. The AST Java class types are
defined by the `scaledmarkets.dabl.node` package in the [Javadocs](https://scaledmarkets.github.io/dabl/).

For an example
of accessing the object model, see the [piper project](https://github.com/Scaled-Markets/piper).
Piper is a build system based on DABL. The [behavioral test suite](https://github.com/ScaledMarkets/dabl/tree/master/test)
also illustrates how to access the AST.

