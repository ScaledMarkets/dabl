# language: en

Feature: TestImport
	
	@done
	Scenario: Simple
		When I import another namespace
		Then the elements of the namespace are accessible
		
	@done
	Scenario: Import complex namespace, eg., a.b,c
		When I import a multi-level namespace
		Then the elements of the namespace are accessible
	
	@done
	Scenario: Test that circular namespace references are detected
		When a namespace imports a second namespace, and that imports a third, which in turn imports the first
		Then an error is generated
		