# DABL Concepts

## DABL is not a scripting language

* It is object oriented, not string oriented.
* It is designed to enable teams to create highly reusable and highly maintainable
build programs.
* It is not a general purpose language: it intentionally does not provide features
for general computation.

## What a `namespace` is

The things in a DABL file comprise a namespace. The namespace name is declared
at the top of the file. All items in the file are scoped to that namespace.

A namespace can import other namespaces. Imported namespaces are accessible
to the importing namespace. To access them, you must prefix them with their
namespace name.

## What a `task` is

The most important things in a namespace are the tasks that it defines. Tasks
do work — they define the steps required to perform some action, such as
compiling a set of files.

Tasks are containers — when a task executes, a new container is created, using the
base image specified by the `dabl.task_container_image_name` DABL setting
(see [Using the Reference Implementation](/README.md#using-the-reference-implementation)).

Tasks are isolated: all inputs come in via the `inputs`, and all outputs exit
via the `outputs`. It is possible to bypass the isolation, but doing so reduces
the benefits of DABL, so only do it when necessary.

## How tasks work — what triggers them, etc.

A task may define a `when` condition. This is a logical expression. A task's
when condition is checked when the task's inputs have changed. If the when
condition is true, the task executes. If a task does not specify a when condition,
then an implicit when condition is used, such that the task executes if any
of its inputs are newer than its outputs.
