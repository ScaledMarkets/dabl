# Function Declaration
A function declaration defines a function that is callable from the DABL file,
but that is implemented in an external runtime, such as a Groovy package. The
function declaration specifies the parameter types and return type that will be
used when a DABL call is made to the function. Type conversions are performed
if appropriate, or a compile-time error is generated if a type conversion is not
appropriate. All types must be serializable, since the function will be called
through an inter-process communication mechanism.

Functions that are defined in this way can be used as DABL verbs anywhere in the
file. In a function call, parameters are positional and comma-separated, and
parentheses are not used to delineate them.
