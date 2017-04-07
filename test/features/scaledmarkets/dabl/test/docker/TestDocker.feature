# language: en

@docker
Feature: TestDocker
	
	@done
	Scenario: Simple
		When I make a ping request to docker
		Then the response is a success

	Scenario: Create container
		When I make a create container request to docker
		Then the response is a success
	
	Scenario: Start container
		Given that I have created two containers
		When I request to start a container
		Then the response is a success
		And the container that I started is running
	
	Sceanrio: Stop container
		Given that I have created two containers and both are running
		When I request to stop a container
		Then the response is a success
		And the container that I stopped is no longer running
	
	Sceanrio: Destroy containers
		Given that I have created two containers and one is running
		When I request to destroy the stopped container
		Then the response is a success
		And the destroyed container no longer exists
	
	Scenario: Get containers
		Given that I have created two containers and one is running
		When I request a list of the containers
		Then the response lists both containers
