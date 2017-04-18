# language: en

Feature: UnitTestOperateOnArtifacts

	Scenario: Basic
		Given a dummy repo
		When a single outputs specifies a single file
		Then the result specifies that file

	Scenario: Wildcard
		Given a dummy repo
		When a single outputs specifies a wildcard pattern
		Then the result specifies that pattern
	
	Scenario: Multiple inputs
		Given a dummy repo
		When two outputs are specified
		Then the result specifies both of the patterns
	