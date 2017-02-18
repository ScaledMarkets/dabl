# language: en

Feature: TestArtifacts
	
	@done
	Scenario: Name cannot conflict with another name of any kind
		When a repo has the same name as the artifact
		Then a SymbolEntryPresent error is generated when we process it

	@notdone
	Scenario: Cannot assert compability with itself
		When an artifact assumes compatibility with itself
		Then an error is generated when we process it

	@notdone
	Scenario: Cannot assert tested with itself
		When an artifact asserts tested with itself
		Then an error is generated when we process it

	@notdone
	Scenario: Wildcard in compatible major version number
		When an artifact asserts compatibility with major version XYZ:*.3
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Wildcard in compatible minor version number
		When an artifact asserts compatibility with minor version XYZ:3.*
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Wildcard in compatible minor version number
		When an artifact asserts compatibility with major and minor version XYZ:*.*
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Range in compatible major version number
		When an artifact asserts compatibility with a range of versions, e.g., 3.3-3.4
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Range in compatible minor version number
		When an artifact asserts compatibility with a range of minor versions
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Wildcard in tested with major version number
		When an artifact asserts tested with a wildcard major version
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Wildcard in tested with minor version number
		When an artifact asserts tested with a wildcard minor version
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Range in tested with major version number
		When an artifact asserts tested with a range of versions, e.g., 3.3-3.4
		Then a correct compatibility spec is generated

	@notdone
	Scenario: Range in tested with minor version number
		When an artifact asserts tested with a range of minor versions
		Then a correct compatibility spec is generated
