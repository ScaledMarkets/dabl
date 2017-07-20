# language: en

@docker
Feature: TestDocker
	
	@done
	Scenario: Simple
		When I make a ping request to docker
		Then the response is a success
	
	@done
	Scenario: List images
		When I get a list of the images
		Then the list size is greater than 0
	
	@done
	Scenario: Create container
		When I make a create container request to docker
		Then the response is a success
	
	@done
	Scenario: Start container
		Given that I have created two containers that do nothing
		When I request to start a container
		Then the response is a success
		And the container that I started is running
	
	@done
	Scenario: Stop container
		Given that I have created two containers and both are running
		When I request to stop a container
		Then the response is a success
		And the container that I stopped is no longer running
	
	@done
	Scenario: Destroy containers
		Given that I have created two containers and one is running
		When I request to destroy the stopped container
		Then the response is a success
		And the destroyed container no longer exists
	
	Scenario: Get containers
		Given that I have created three containers
		When I request a list of the containers
		Then the response lists all three containers
