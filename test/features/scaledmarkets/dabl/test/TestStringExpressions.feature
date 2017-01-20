# language: en

Feature: TestStringExpressions
	
	@done
	Scenario: Simple
		When I compile a static string expression
		Then the expression value can be retrieved and is correct
