# language: en

Feature: TestImport
	
	@done
	Scenario: Simple
		When I import another namespace
		Then the elements of the namespace are accessible
