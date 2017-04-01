package scaledmarkets.dabl.test.docker;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.Dabl;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;

public class TestDocker extends TestBase {
	
	@Before
	public void beforeEachScenario() throws Exception {
	}
	
	@After
	public void afterEachScenario() throws Exception {
	}
	
	@When("^I make a ping request to docker$")
	public void i_make_a_ping_request_to_docker() throws Exception {
		
		Docker docker = Docker.connect();
		String response = docker.ping();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
		
	}
}
