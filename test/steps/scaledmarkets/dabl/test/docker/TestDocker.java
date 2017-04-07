package scaledmarkets.dabl.test.docker;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.docker.*;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;

public class TestDocker extends TestBase {
	
	// Scenario: Simple
	@When("^I make a ping request to docker$")
	public void i_make_a_ping_request_to_docker() throws Exception {
		
		Docker docker = Docker.connect();
		String response = docker.ping();
		docker.close();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		
		throw new Exception();
	}


	// Scenario: Create container
	@When("^I make a create container request to docker$")
	public void i_make_a_create_container_request_to_docker() throws Exception {
		throw new Exception();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		throw new Exception();
	}
	
	
	// Scenario: Start container
	@Given("^that I have created two containers$")
	public void that_i_have_created_two_containers() throws Exception {
		throw new Exception();
	}
	
	@When("^I request to start a container$")
	public void i_request_to_start_a_container() throws Exception {
		throw new Exception();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		throw new Exception();
	}
	
	@And("^the container that I started is running$")
	public void the_container_that_i_started_is_running() throws Exception {
		throw new Exception();
	}
	
	
	// Sceanrio: Stop container
	@Given("^that I have created two containers and both are running$")
	public void that_i_have_created_two_containers_and_both_are_running() throws Exception {
		throw new Exception();
	}
	
	@When("^I request to stop a container$")
	public void i_request_to_stop_a_container() throws Exception {
		throw new Exception();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		throw new Exception();
	}
	
	@And("^the container that I stopped is no longer running$")
	public void the_container_that_i_stopped_is_no_longer_running() throws Exception {
		throw new Exception();
	}
	
	
	// Sceanrio: Destroy containers
	@Given("^that I have created two containers and one is running$")
	public void that_i_have_created_two_containers_and_one_is_running() throws Exception {
		throw new Exception();
	}
	
	@When("^I request to destroy the stopped container$")
	public void i_request_to_destroy_the_stopped_container() throws Exception {
		throw new Exception();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		throw new Exception();
	}
	
	@And("^the destroyed container no longer exists$")
	public void the_destroyed_container_no_longer_exists() throws Exception {
		throw new Exception();
	}
	
	
	// Scenario: Get containers
	@Given("^that I have created two containers and one is running$")
	public void that_i_have_created_two_containers_and_one_is_running() throws Exception {
		throw new Exception();
	}
	
	@When("^I request a list of the containers$")
	public void i_request_a_list_of_the_containers() throws Exception {
		throw new Exception();
	}
	
	@Then("^the response lists both containers$")
	public void the_response_lists_both_containers() throws Exception {
		throw new Exception();
	}
}
