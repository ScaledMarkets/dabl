# language: en

@analyzer
Feature: TestNewline
	
	@done
	Scenario: NamespaceNewline
		When a namespace decl ends with a newline
		Then the namespace compiles without error
	
