# language: en

@docker
Feature: TestDocker
	
	@done
	Scenario: Simple
		When I make a ping request to docker
		Then the response is a success

