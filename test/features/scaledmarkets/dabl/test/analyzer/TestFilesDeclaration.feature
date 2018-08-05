# language: en

@analyzer
Feature: TestFilesDeclaration
	
	@done
	Scenario: Simple
		When I declare a file set
		Then I can reference the file set elsewhere
