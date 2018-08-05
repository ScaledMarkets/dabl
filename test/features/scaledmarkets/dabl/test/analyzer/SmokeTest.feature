# language: en

@analyzer
Feature: SmokeTest
	
	@done @smoke
	Scenario: Smoke
		When I compile trivial input
		Then the compiler returns without an error
