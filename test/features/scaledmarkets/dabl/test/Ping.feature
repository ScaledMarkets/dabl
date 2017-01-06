# language: en

Feature: Ping
	
	@done
	Scenario: Ping
		When I send a Ping request
		Then the ping HTTP response code should be 200
