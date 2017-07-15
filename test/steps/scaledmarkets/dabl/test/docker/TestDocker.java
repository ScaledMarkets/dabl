package scaledmarkets.dabl.test.docker;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.docker.*;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;

public class TestDocker extends TestBase {
	
	private Docker docker;
	private DockerContainer container1;
	private DockerContainer container2;
	private DockerContainer[] containers;
	private boolean initialized = false;
	private List<List<String>> allImages;
	
	protected void initOnce() throws Exception {
		if (initialized) return;
		initialized = true;
		Docker docker = Docker.connect();
		docker.destroyContainers("*", null);
		docker.close();
	}
	
	@Before("@docker")
	public void beforeEachScenario() throws Exception {
		//(new Exception("In TestDocker.beforeEachScenario")).printStackTrace();
		initOnce();
		this.docker = Docker.connect();
	}
	
	@After("@docker")
	public void afterEachScenario() throws Exception {
		docker.destroyContainers("*", null);
		docker.close();
	}

	
	// Scenario: Simple
	@When("^I make a ping request to docker$")
	public void i_make_a_ping_request_to_docker() throws Exception {
		String response = docker.ping();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
	}
	
	// Scenario: List images
	@When("^I get a list of the images$")
	public void i_get_a_list_of_the_images() throws Exception {
		this.allImages = docker.getImages();
		
		// debug
		System.out.println("Images:");
		for (List<String> image : this.allImages) {
			System.out.print("\t");
			for (String name : image) {
				System.out.print(name + ", ");
			}
			System.out.println();
		}
		// end debug
	}
	
	@Then("^the list size is greater than 0$")
	public void the_list_size_is_greater_than_0() throws Exception {
		assertThat(this.allImages.size() > 0, "Did not find any images");
	}

	// Scenario: Create container
	@When("^I make a create container request to docker$")
	public void i_make_a_create_container_request_to_docker() throws Exception {
		DockerContainer container = docker.createContainer("alpine:latest", "mycontainer",
			null, null, false, null);
		docker.destroyContainers("mycontainer", null);
	}
	
	
	// Scenario: Start container
	@Given("^that I have created two containers$")
	public void that_i_have_created_two_containers() throws Exception {
		this.container1 = docker.createContainer("alpine", "MyContainer1",
			null, null, false, null);
		this.container2 = docker.createContainer("alpine", "MyContainer2",
			null, null, false, null);
	}
	
	@When("^I request to start a container$")
	public void i_request_to_start_a_container() throws Exception {
		this.container1.start("");  // ....
	}
	
	@And("^the container that I started is running$")
	public void the_container_that_i_started_is_running() throws Exception {
		assertThat(this.container1.isRunning(), "container1 is not running");
	}
	
	
	// Sceanrio: Stop container
	@Given("^that I have created two containers and both are running$")
	public void that_i_have_created_two_containers_and_both_are_running() throws Exception {
		this.container1 = docker.createContainer("alpine", "MyContainer1",
			null, null, false, null);
		this.container2 = docker.createContainer("alpine", "MyContainer2",
			null, null, false, null);
		this.container1.start("");  // ....
		this.container2.start("");  //....
		assertThat(this.container1.isRunning(), "container1 is not running");
		assertThat(this.container2.isRunning(), "container2 is not running");
	}
	
	@When("^I request to stop a container$")
	public void i_request_to_stop_a_container() throws Exception {
		this.container1.stop();
	}
	
	@And("^the container that I stopped is no longer running$")
	public void the_container_that_i_stopped_is_no_longer_running() throws Exception {
		assertThat(! this.container1.isRunning(), "container1 is running");
		assertThat(this.container2.isRunning(), "contaienr2 is not running");
	}
	
	
	// Sceanrio: Destroy containers
	@Given("^that I have created two containers and one is running$")
	public void that_i_have_created_two_containers_and_one_is_running() throws Exception {
		this.container1 = docker.createContainer("alpine", "MyContainer1",
			null, null, false, null);
		this.container2 = docker.createContainer("alpine", "MyContainer2",
			null, null, false, null);
		this.container1.start(""); // ....
		assertThat(this.container1.isRunning(), "container1 is not running");
		assertThat(! this.container2.isRunning(), "container2 is running");
	}
	
	@When("^I request to destroy the stopped container$")
	public void i_request_to_destroy_the_stopped_container() throws Exception {
		this.container2.destroy();
	}
	
	@And("^the destroyed container no longer exists$")
	public void the_destroyed_container_no_longer_exists() throws Exception {
		assertThat(container1.exists(), "container1 does not exist");
		assertThat(! container2.exists(), "container2 exists");
	}
	
	
	// Scenario: Get containers
	@When("^I request a list of the containers$")
	public void i_request_a_list_of_the_containers() throws Exception {
		this.containers = this.docker.getContainers();
	}
	
	@Then("^the response lists both containers$")
	public void the_response_lists_both_containers() throws Exception {
		List<String> ids = new LinkedList<String>();
		for (DockerContainer container : this.containers) {
			ids.add(container.getContainerId());
		}
		
		assertThat(ids.contains(this.container1.getContainerId()) &&
			ids.contains(this.container2.getContainerId()),
			"response does not list both containes");
	}
}
