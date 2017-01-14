# language: en

Feature: TestTask
	
	@done
	Scenario: Simple
		When I compile a simple task
		Then I can retrieve the the task by its name
