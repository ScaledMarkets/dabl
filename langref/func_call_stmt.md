# Function Call Statement

## Purpose

A function call statement enables a DABL script to invoke an external function
that is written in a programming language such as Java or Groovy. The function
must have been declared by a [function declaration](function_decl.md).

## Syntax

A function call statement has the form,

<dl>
<dd>[<i>variable</i> =] <i>function-name</i> [<i>args</i>...]</dd>
</dl>

where 
<dl>
<dd><i>variable</i> is an optional variable that is defined by the statement,</dd>
<dd><i>function-name</i> is the name of the function, as declared in a
[function declaration](function_decl.md), and</dd>
<dd><i>args</i> is a sequence of zero or more argument values.
Arguments are <i>not</i> separated by commas.</dd>
</dl>

Argument values can be any [expression](expression.md), but may not contain a function call. A
function call can only appear in a function call statement.

Prepositional words can be inserted into the sequence of arguments, to provide
clarity in the purpose of the arguments. For example, the <code>post</code> function takes
two arguments, but can be called this way:

```
post abc.jar to my_maven/myproject/abc.jar
```

In this example, the word `to` is a preposition that is ignored by the DABL parser.
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

