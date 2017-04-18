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
	
	@done @exec
	Scenario: Execute bash
		Given I have a task containing bash commands
		When I compile the task
		Then the bash commands are executed
	
	@done @exec
	Scenario: Basic pushing inputs
	
	@done @exec
	Scenario: Basic retrieving outputs

	@done @exec
	Scenario: Complex pushing inputs
	
	@done @exec
	Scenario: Complex retrieving outputs
	