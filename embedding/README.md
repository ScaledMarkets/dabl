# Embedding the Compiler In an Application

## What the Compiler Produces As Output

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

## Helper Class

The class [`scaledmarkets.dabl.helper.Helper`](https://scaledmarkets.github.io/dabl/scaledmarkets/dabl/helper/Helper.html)
provides helper methods for accessing the AST and its annotations.
You do not need to use the `Helper` class, but it greatly simplifies access
to the AST.

## Examples

For an example of instantiating the compiler class, running it to process input, and
then accessing the resulting object model, see the
[piper project](https://github.com/Scaled-Markets/piper).
Piper is a build system based on DABL.
Another example of instantiating and using the compiler is
the [`Dabl`](https://github.com/ScaledMarkets/dabl/blob/master/java/scaledmarkets/dabl/analysis/Dabl.java)
class itself, since its `main` method must instantiate the compiler class.
The [behavioral test suite](https://github.com/ScaledMarkets/dabl/tree/master/test)
provides additional examples.
