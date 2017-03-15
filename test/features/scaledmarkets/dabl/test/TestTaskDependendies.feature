# language: en

@exec
Feature: TestTaskDependendies
	
	@done
	Scenario: Two tasks
		Given I have tasks A and B and A has an output that is an input to B
		When I compile them in simulate mode
		Then I can verify that B depends on A
	