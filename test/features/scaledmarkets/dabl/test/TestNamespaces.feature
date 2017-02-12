# language: en

Feature: TestNamespaces
	
	@done
	Scenario: Test that circular namespace references are detected
		When a namespace imports a second namespace, and that imports a third, which in turn imports the first
		Then an error is generated
		