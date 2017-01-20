# language: en

Feature: TestFunctionDeclaration
	
	@done
	Scenario: Simple
		When I declar a function
		Then I can call the function and obtain a correct result
