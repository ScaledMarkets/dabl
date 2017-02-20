# language: en

Feature: TestForwardReferences
	
	@notdone
	Scenario: Simple
		When I declare a symbol after it is referenced
		Then the IdRefAnnotation is correct
