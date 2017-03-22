# language: en

Feature: TestStringExpressions
	
	@notdone
	Scenario: Simple
		When I compile a static string expression
		Then the expression value can be retrieved and is correct

	@notdone
	Scenario: Multi-line string
		When a multi-line string is processed
		Then the full string value is obtainable from the AST
		
	@notdone
	Scenario: String with embedded quotes
		When a triple-quote string is used
		Then one can obtain the full string value from the AST
		