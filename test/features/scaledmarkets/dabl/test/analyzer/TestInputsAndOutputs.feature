# language: en

Feature: TestInputsAndOutputs
	
	@done
	Scenario: Simple
		When I compile a task that has inputs and outputs
		Then the inputs and outputs are retrievable
