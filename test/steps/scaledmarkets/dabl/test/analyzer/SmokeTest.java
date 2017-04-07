package scaledmarkets.dabl.test.analyzer;

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

public class SmokeTest extends TestBase {
	
	@Before
	public void beforeEachScenario() throws Exception {
	}
	
	@After
	public void afterEachScenario() throws Exception {
	}
	
	@When("^I compile trivial input$")
	public void i_compile_trivial_input() throws Exception {
		
		System.out.println("LD_LIBRARY_PATH=" + System.getenv("LD_LIBRARY_PATH"));

		
		Reader reader = new StringReader(
"\n namespace simple task t123"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
	}
	
	@Then("^the compiler returns without an error$")
	public void the_compiler_returns_without_error() throws Exception {
	}
}
