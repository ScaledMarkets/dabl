# language: en

@task
Feature: TestTaskRuntime
	
	@done
	Scenario: Simple
		When a task should execute
		Then the task executes
	
