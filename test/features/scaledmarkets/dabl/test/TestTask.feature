# language: en

Feature: TestTask
	
	@done
	Scenario: Simple
		When I compile a simple task
		Then I can retrieve the the task by its name

	@done @exec
	Scenario: Two tasks
		Given I have two tasks and one has an output that is an input to the other
		When I compile them in simulate mode
		Then I can verify the task dependency
	