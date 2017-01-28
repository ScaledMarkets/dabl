# Function Declaration

## Purpose

A function declaration defines a function that is callable from the DABL file,
but that is implemented in an external runtime, such as a Groovy package. The
function declaration specifies the parameter types and return type that will be
used when a DABL call is made to the function. Type conversions are performed
if appropriate, or a compile-time error is generated if a type conversion is not
appropriate. All types must be serializable, since the function will be called
through an inter-process communication mechanism. Runtime failures in a
[function call](func_call_stmt.md) should not affect the DABL runtime, although
this is not a guarantee.

Functions that are defined in this way can be used as
[procedural statements](procedural_stmt.md)
inside [task declarations](task_decl.md). In a function call, parameters are
positional, and parentheses are not used to delineate them.

In a function declaration, the parameters are comma-separated. However, when
calling the function (see [Function Call Statement](func_call_stmt.md)), the
actual parameters are <i>not</i> comma-separated.

A can specify that certain prepositional words appear in an argument list. When 
this is done, the preposition appears in the formal argument list in lieu of
a comma separator. When calling such a function, the function call must specify
those same prepositions, in the same positions, but without commas.
The set of ignored preopsitions is as follows:

* from
* to
* in
* for
* with
* when
* on
* of
* than

A function must have at least one argument.

## Syntax

A function declaration has the following syntax:

<dl>
<dd>
<code>function</code> <i>name</i> [ <i>type</i> [ , <i>type</i>... ] ]
	<code>binds to</code> <i>target-language</i>
	<code>method</code> <i>native-method-name</i> <code>returns</code> <i>type</i>
</dd>
</dl>

## Example

```
function sign string, string binds to "groovy" method
    "com.scaledmarkets.utils.Signature.sign" returns array of byte
```
## Example 2

```
function convert string to string binds to "groovy" method
    "com.abc.Converter.convert" returns string
```
