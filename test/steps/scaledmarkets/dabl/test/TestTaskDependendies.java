package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.node.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestTaskDependencies extends TestBase {
	
	@Given("^I have two tasks and one has an output that is an input to the other$")
	public void i_have_two_tasks_and_one_has_an_output_that_is_an_input_to_the_other() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@When("^I compile them in simulate mode$")
	public void i_compile_them_in_simulate_mode() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can verify the task dependency$")
	public void i_can_verify_the_task_dependency() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
}
