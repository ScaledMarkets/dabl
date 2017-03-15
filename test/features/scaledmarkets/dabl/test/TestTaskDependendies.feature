# language: en

@exec
Feature: TestTaskDependendies
	
	@done
	Scenario: Two tasks
		Given I have two tasks and one has an output that is an input to the other
		When I compile them in simulate mode
		Then I can verify the task dependency
	