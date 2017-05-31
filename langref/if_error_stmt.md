# If Error Statement

## Purpose

The `if error` statement installs an error handler in a task.

## Syntax

`if error` [*procedural_stmts*](procedural_stmt.md) `end if`

## Semantics

If an error occurs while performing a task procedural statement, the default
behavior is to terminate the task. However, an error handler can be installed
using the `if error` keyword phrase. The error handler is triggered when a
function call returns an error status: the error status mechanism of the function
is specific to the language that the function is written in.

Triggering an error handler results in
the execution of the handler's procedural statements. 

Once an error handler is defined via an `if error` statement, the error handler
remains in place for the lifetime of the task execution, until another error
handler takes its place.

If an error occurs during execution of the handler's procedural statements,
the handler is terminated, and then the task is terminated.

If an error handler's procedural statements execute without error, the task's
process status is set to 0. Otherwise, the task's final process status is non-zero.

An error handler sequence of procedural statements may contain an `if error` statement.
If so, that `if error` defines a new error handler that permantently replaces the
one that is currently installed.

If a function call within an error handler returns a value (i.e., has a target),
the value is scoped to the task.
