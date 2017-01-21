package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.main.*;
import java.io.Reader;
import java.io.StringReader;
import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class TestFunctionDeclaration extends TestBase {
	
	CompilerState state;
	
	@When("^I declare a function$")
	public void i_declare_a_function() throws Exception {
		
		Reader reader = new StringReader(
""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
		
	}
	
	@Then("^I can call the function and obtain a correct result$")
	public void i_can_call_the_function_and_obtain_a_correct_result() throws Exception {
		
		
	}
}
