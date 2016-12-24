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
positional and comma-separated, and
parentheses are not used to delineate them.
