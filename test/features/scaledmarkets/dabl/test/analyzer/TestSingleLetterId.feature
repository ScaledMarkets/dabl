# language: en

Feature: TestSingleLetterId
	
	@done
	Scenario: SingleLetterTaskName
		When a task name is a single character
		Then the namespace compiles without error
	
