package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.main.Dabl;
import java.io.StringReader;

public class SmokeTest extends TestBase {
	
	@When("^I compile trivial input$")
	public void i_compile_trivial_inputs() throws Exception {
		
		Reader reader = new StringReader(
			"\n         namespace simple import abc      \n"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		dabl.process();
	}
	
	@Then("^the compiler returns without an error")
	public void the_compiler_returns_without_error() throws Exception {
	}
}
