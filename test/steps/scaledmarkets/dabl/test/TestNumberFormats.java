package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.main.*;
import scaledmarkets.dabl.node.*;

import java.io.Reader;
import java.io.StringReader;
import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class TestNumberFormats extends TestBase {
	
	Reader reader;

	@Before
	public void beforeEachScenario() throws Exception {
		reader = null;
	}
	

	// Test whole number
	
	@When("^a whole number is processed$")
	public void a_whole_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can retrieve the numeric value$")
	public void i_can_retrieve_the_numeric_value() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test negative whole number

	@When("^a negative whole number is processed$")
	public void a_negative_whole_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test decimal number
	
	@When("^a decimal number is processed$")
	public void a_decimal_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test negative decimal number

	@When("^a negative decimal number is processed$")
	public void a_negative_decimal_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test simple numeric expression

	@When("^a simple numeric expression is processed$")
	public void a_simple_numeric_expression_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can retrieve the values and operators$")
	public void i_can_retrieve_the_values_and_operators() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test simple numeric expression with a negative number

	@When("^a simple numeric expression with a negative number is processed$")
	public void a_simple_numeric_expression_with_a_negative_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test numeric pattern with two decmial points

	@When("^a numeric pattern with two decmial points is processed$")
	public void a_numeric_pattern_with_two_decmial_points_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can retrieve the pattern components$")
	public void i_can_retrieve_the_pattern_components() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test numeric pattern with a wildcard

	@When("^a numeric pattern with a wildcard is processed$")
	public void a_numeric_pattern_with_a_wildcard_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
}
