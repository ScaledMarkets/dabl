# language: en

@exec
Feature: TestTask
	
	@done
	Scenario: Simple
		When I compile a simple task
		Then I can retrieve the the task by its name

	@done
	Scenario: Two tasks
		Given I have two tasks and one has an output that is an input to the other
		When I compile them in simulate mode
		Then I can verify the task dependency
	
	@done @container
	Scenario: Execute bash
		Given I have a task containing bash commands
		When I compile the task
		Then the bash commands are executed
	
	@done @container
	Scenario: Basic pushing inputs and retrieving outputs
		Given a task containing bash commands that compute the sum of values in an input file
		When I compile the task
		Then the outputs contains a file with the correct sum value
	
	@done @container
	Scenario: Complex pushing inputs and retrieving outputs
		Given a task containing bash commands that read three files that are named in the inputs and two other files that are referenced via a wildcard
		  And the task writes two files by name and two more by wildcard
		  And the task modifies one of the input files
		When I compile the task
		Then the outputs contain the output files, and the input files are unmodified
	
	@done @container
	Scenario: Test that an open container is able to access the host's networks
	
	@done @container
	Scenario: Test that a closed container is not able to access the host's networks
	