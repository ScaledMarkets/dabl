# language: en

@unit @exec
Feature: UnitTestPatternSets

	@done
	Scenario: Test operateOnFiles
		Given a working directory
		When I specify two include files and one exclude file
		Then one file path is processed
	