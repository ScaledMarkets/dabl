# Function Call Statement

## Purpose

A function call statement enables a DABL script to invoke an external function
that is written in a programming language such as Java or Groovy. The function
must have been declared by a [function declaration](function_decl.md).
The function is called during the Execution phase (see
[Processing Phases](https://github.com/Scaled-Markets/dabl/tree/master/langref#processing-phases)).

## Syntax

A function call statement has the form,

<dl>
<dd>[<i>variable</i> <-] <i>function-name</i> [<i>args</i>...]</dd>
</dl>

where 
<dl>
<dd><i>variable</i> is an optional variable that is defined by the statement,</dd>
<dd><i>function-name</i> is the name of the function, as declared in a
[function declaration](function_decl.md), and</dd>
<dd><i>args</i> is a sequence of zero or more argument values.
Arguments are <i>not</i> separated by commas.</dd>
</dl>

Note the use of the `<-` symbol: it is spoken as "gets", to indicate the the variable
*gets* (i.e., receives) the returned function value.

Argument values can be any [expression](expression.md), but may not contain a function call. A
function call can only appear in a function call statement.

A [function declaration](function_decl.md) can specify that certain prepositional
words appear in an argument list. In that case, a function call must specify
those same prepositions, in the same positions. For example,
the <code>post</code> function takes two arguments, but must be called this way:

```
post abc.jar to my_maven/myproject/abc.jar
```

In this example, the word `to` is a preposition that is ignored by the DABL parser.

## Binding to a Function at Runtime

When a DABL Executor encounters a function call, it must locate the resources
needed to load and execute the function. For a Java function, this amounts to
finding an appropriate class loader for loading the Java class in which the function
is implemented. For other langauge functions, other kinds of resources might be
needed. In general, a FunctionHandler must be obtained for the function's language.
The FunctionHandler decides how to obtain the resources that are needed to load
and execute the function. FunctionHandlers can be specified in the `.dabl.container.properties`
file, via the setting for,

<dl>
<dd><code>dabl.function_handler.</code><i>language</i></dd>
</dl>

where *language* is the function's language. For example, DABL's default setting
for Java functions is,

```
dabl.function_handler.java=scaledmarkets.dabl.handlers.JavaFunctionHandler
```

See [Setting Container (Runtime) Properties](https://github.com/ScaledMarkets/dabl#setting-container-task-runtime-properties).

## Built-In Functions

The namespace dabl.standard is implicitly imported in a DABL file.

The following Java functions are pre-defined in dabl.standard:

<dl>
<dd><code>report</code> <i>string-message</i></dd>
<dd><code>bash</code> <i>command-string timeout-seconds</i> - Creates a new process running bash,
	and executes the specified shell command. Blocks until the command completes.
	If the process executes with non-zero status, an exception is thrown, causing
	DABL to set the task status to non-zero.</dd>
<dd><code>powershell</code> <i>command-string</i></dd>
<dd><code>deploy</code> <i>template-file-ref</i> <code>to</code> <i>deployment-ref</i></dd>
<dd><code>ssh</code> <i>target-host command-string</i></dd>
<dd><code>destroy</code> <i>deployment-ref</i></dd>
<dd><code>snapshot</code></dd>
</dl>

The actual specification of package `dabl.standard` can be found
[here](https://github.com/ScaledMarkets/dabl/blob/master/java/scaledmarkets/dabl/analyzer/DablStandard.java).
