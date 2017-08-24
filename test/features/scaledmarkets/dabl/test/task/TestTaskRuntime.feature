# language: en

@task
Feature: TestTaskRuntime
	
	@done
	Scenario: Simple
		Given a trivial task
		When the namespace is processed
		Then the task executes
	
	@done
	Scenario: Test that a true When condition causes task to execute
		Given a task with a true when condition
		When the namespace is processed
		Then the task executes
	
	
	Scenario: Test that a false When condition prevents task from executing
		Given a task with a false when condition
		When the namespace is processed
		Then the task does not execute
	
	Scenario: Test that absence of a When condition behaves in the default manner
		Given a task without a when condition and inputs that are newer than the outputs
		When the namespace is processed
		Then the task executes
	
	Scenario: Test that inputs are available to the task runtime
	
	Scenario: Test that outputs are produced by the task runtime
	
	Scenario: Test that aborting the Executor causes running tasks to abort
	
	Scenario: Test that the Executor obtains the task's final process status
	
	Scenario: Test that a task timeout operates properly
	
	Scenario: Test that a function without arguments gets called properly
	
	Scenario: Test that a function with one argument gets called properly
	
	Scenario: Test that a function with two arguments gets called properly
	
	Scenario: Test that a function int return value is obtainable
	
	Scenario: Test that a function double return value is obtainable
	
	Scenario: Test that a function String return value is obtainable
	
	Scenario: Test that a function array return value is obtainable
	
	
	