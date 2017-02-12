# language: en

Feature: TestForwardReferences
	
	@done
	Scenario: Simple
		When I declare a symbol after it is referenced
		Then I can retrieve the IdRefAnnotation from the reference to the symbol
		And the IdRefAnnotation is correct
