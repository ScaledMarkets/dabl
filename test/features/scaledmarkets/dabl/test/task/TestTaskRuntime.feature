# language: en

@task
Feature: TestTaskRuntime
	
	@done
	Scenario: Simple
		Given Docker is running
		When a task should execute
		Then the task executes
	
	Scenario: Test Timeout
	