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

public class TestFilesDeclaration extends TestBase {
	
	CompilerState state;
	
	@When("^I declare a file set$")
	public void i_declare_a_file_set() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple" +
"  files Stuff from \"myrepo\" in Repo1\n" +
"  include \"*.java\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
	}
	
	@Then("^I can reference the file set elsewhere$")
	public void i_can_reference_the_file_set_elsewhere() throws Exception {
		
		
		assertThat(false);
	}
}
