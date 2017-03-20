# If Error Statement

If an error occurs while performing a task procedural statement, the default
behavior is to terminate the task. However, an error handler can be installed
using the `if error` keyword phrase. The error handler is triggered if the current
process status of the task is non-zero. This applies when the on error statement
is executed as well as any time afterwards, until another on error statement is executed.
