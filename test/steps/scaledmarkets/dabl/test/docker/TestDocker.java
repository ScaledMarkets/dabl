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
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;
import java.util.List;
import java.util.LinkedList;

public class TestDocker extends TestBase {
	
	private Docker docker;
	private DockerContainer container41, container51, container61, container71;
	private DockerContainer container42, container52, container62, container72, container73;
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

	
	// Scenario 1: Simple
	@When("^I make a ping request to docker$")
	public void i_make_a_ping_request_to_docker() throws Exception {
		String response = docker.ping();
	}
	
	@Then("^the response is a success$")
	public void the_response_is_a_success() throws Exception {
	}
	
	// Scenario 2: List images
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

	// Scenario 3: Create container
	@When("^I make a create container request to docker$")
	public void i_make_a_create_container_request_to_docker() throws Exception {
		DockerContainer container = docker.createContainer("alpine:latest", "MyContainer31",
			null, null, false, null);
		docker.destroyContainers("MyContainer31", null);
	}
	
	
	// Scenario 4: Start container
	@Given("^that I have created two containers that do nothing$")
	public void that_i_have_created_two_containers() throws Exception {
		this.container41 = docker.createContainer("alpine", "MyContainer41",
			null, null, false, null);
		this.container42 = docker.createContainer("alpine", "MyContainer42",
			null, null, false, null);
	}
	
	@When("^I request to start a container$")
	public void i_request_to_start_a_container() throws Exception {
		this.container41.start("");  // ....
	}
	
	@And("^the container that I started has exited$")
	public void the_container_that_i_started_has_exited() throws Exception {
		assertThat(this.container41.exited(), "container41 does not have status 'exited'");
	}
	
	
	// Sceanrio 5: Stop container
	@Given("^that I have created two containers and both are running$")
	public void that_i_have_created_two_containers_and_both_are_running() throws Exception {
		this.container51 = docker.createContainer("alpine", "MyContainer51",
			null, null, false, null);
		this.container52 = docker.createContainer("alpine", "MyContainer52",
			null, null, false, null);
		InputStream inputStream51 = this.container51.start("");  // ....
		System.out.println("Started container 51");  // debug
		/*
		BufferedReader br51 = new BufferedReader(new InputStreamReader(inputStream51));
		for (;;) {
			String line = br51.readLine();
			if (line == null) break;
			System.out.println(line);
		}
		//br51.close();
		*/
		
		InputStream inputStream52 = this.container52.start("");  //....
		System.out.println("Started container 52");  // debug
		/*
		BufferedReader br52 = new BufferedReader(new InputStreamReader(inputStream51));
		for (;;) {
			String line = br52.readLine();
			if (line == null) break;
			System.out.println(line);
		}
		//br52.close();
		*/
		
		assertThat(this.container51.isRunning(), "container51 is not running");
		System.out.println("Container 51 is running"); // debug
		assertThat(this.container52.isRunning(), "container52 is not running");
		System.out.println("Container 52 is running"); // debug
	}
	
	@When("^I request to stop a container$")
	public void i_request_to_stop_a_container() throws Exception {
		System.out.println("Stopping container 51"); // debug
		this.container51.stop();
		System.out.println("Container 51 stopped."); // debug
	}
	
	@And("^the container that I stopped is no longer running$")
	public void the_container_that_i_stopped_is_no_longer_running() throws Exception {
		assertThat(! this.container51.isRunning(), "container51 is running");
		assertThat(this.container52.isRunning(), "container52 is not running");
	}
	
	
	// Sceanrio 6: Destroy containers
	@Given("^that I have created two containers and one is running$")
	public void that_i_have_created_two_containers_and_one_is_running() throws Exception {
		this.container61 = docker.createContainer("alpine", "MyContainer61",
			null, null, false, null);
		this.container62 = docker.createContainer("alpine", "MyContainer62",
			null, null, false, null);
		this.container61.start(""); // ....
		assertThat(this.container61.isRunning(), "container61 is not running");
		assertThat(! this.container62.isRunning(), "container62 is running");
	}
	
	@When("^I request to destroy the stopped container$")
	public void i_request_to_destroy_the_stopped_container() throws Exception {
		this.container62.destroy();
	}
	
	@And("^the destroyed container no longer exists$")
	public void the_destroyed_container_no_longer_exists() throws Exception {
		assertThat(container61.exists(), "container61 does not exist");
		assertThat(! container62.exists(), "container62 exists");
	}
	
	
	// Scenario 7: Get containers
	@Given("^that I have created three containers$")
	public void that_I_have_created_three_containers() throws Exception {
		this.container71 = docker.createContainer("alpine", "MyContainer71",
			null, null, false, null);
		this.container72 = docker.createContainer("alpine", "MyContainer72",
			null, null, false, null);
		this.container73 = docker.createContainer("alpine", "MyContainer73",
			null, null, false, null);
	}
	
	@When("^I request a list of the containers$")
	public void i_request_a_list_of_the_containers() throws Exception {
		this.containers = this.docker.getContainers();
	}
	
	@Then("^the response lists all three containers$")
	public void the_response_lists_all_three_containers() throws Exception {
		List<String> ids = new LinkedList<String>();
		for (DockerContainer container : this.containers) {
			ids.add(container.getContainerId());
		}
		
		assertThat(ids.contains(this.container71.getContainerId()) &&
			ids.contains(this.container72.getContainerId()) &&
			ids.contains(this.container73.getContainerId()),
			"response does not list all three containers");
	}
}
