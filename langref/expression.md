# Expressions

## Syntax

Expressions are evaluated when the DABL file is processed. As such, expressions
must be statically evaluatable.

An expression may contain a variable that is defined by a 
[function call statement](func_call_stmt.md), but an expression cannot directly
contain a function call.

Expressions can evaluate to any of these types:

* string
* numeric (integer or float)
* logical

DABL automatically converts between numeric types as needed.

String operators such as concatenation are not supported.

Numeric expressions can be composed of addition (+, -) and multiplicative (*, /)
operations. Multiplicative operations take precedence over additive operations.
Otherwise, grouping occurs left to right. Thus,

```
1 + 2 + 3
```

is equivalent to

```
(1 + 2) + 3
```

Logical expressions can be composed of `and` and `or` operations. There is no
precedence among logical operations: grouping occurs left to right.

Expressions can be grouped via parentheses.
