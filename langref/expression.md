# Expressions

## Syntax

Expressions that occur outside of a task are evaluated when the DABL file is
processed. As such, expressions outside of tasks must be statically evaluatable.

Within a task, an expression may contain a variable that is defined by a 
[function call statement](func_call_stmt.md), but an expression cannot directly
contain a function call.

Expressions can evaluate to any of these types:

* `string`
* `numeric` (integer or floating point)
* `logical`
* `array of` *type*

DABL automatically converts between numeric types as needed: thus, all numbers
are considered to be the same type, `numeric`.

The only string operation that is supported is the catenation operator, `^`.

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

The binary equality and inequality operators `=` and `!=`, respectively,
can be used to compare any two values, producing a logical result.
The binary comparison operators `>`, `<`,
`>=`, and `<=` can be used to compare any two numeric values, producing a logica result.

Expressions can be grouped via parentheses.

## Arrays

Array literals may not be defined in DABL, but function argument types and return types may
be specified to be arrays.

Arrays may only be one dimensional. However, the elements of an array may be arrays.

Array elements may be selected using the syntax,

*array-name*`[` *index* `]`

Thus, for example, the following selects the fifth element of array A:

A`[`5`]`

The length of an array may be obtained by the syntax,

*array-name*.`length`

For example, the length of array A can be obtained via,

`A.length`

## Contextual Restrictions

A function return variable value can only be referenced from within a task.

A `newer than` or `older than` expression can only appear outside of a task.

A `succeeded` or `failed` expression can only appear outside of a task.
