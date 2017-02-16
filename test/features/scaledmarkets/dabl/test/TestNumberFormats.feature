# language: en

Feature: TestNumberFormats
	
	@done
	Scenario: Test whole number
		When a whole number is processed
		Then I can retrieve the numeric value
	
	Scenario: Test negative whole number
		When a negative whole number is processed
		Then I can retrieve the numeric value
	
	Scenario: Test decimal number
		When a decimal number is processed
		Then I can retrieve the numeric value
	
	Scenario: Test negative decimal number
		When a negative decimal number is processed
		Then I can retrieve the numeric value
	
	Scenario: Test simple numeric expression
		When a simple numeric expression is processed
		Then I can retrieve the values and operators
	
	Scenario: Test simple numeric expression with a negative number
		When a simple numeric expression with a negative number is processed
		Then I can retrieve the values and operators
	
	Scenario: Test numeric pattern with two decmial points
		When a numeric pattern with two decmial points is processed
		Then I can retrieve the pattern components
	
	Scenario: Test numeric pattern with a wildcard
		When a numeric pattern with a wildcard is processed
		Then I can retrieve the pattern components
