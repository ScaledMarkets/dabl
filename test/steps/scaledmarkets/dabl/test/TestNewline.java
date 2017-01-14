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

public class TestNewline extends TestBase {
	
	CompilerState state;
	
	@When("^a namespace decl ends with a newline$")
	public void a_namespace_decl_ends_with_a_newline() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple\n" + // no space between 'simple' and the newlilne
"task t123\n" +
"  when true\n" +
"  inputs \"x\" from \"repo1\" in my_git\n" +
"  outputs \"y\" from \"repo2\" in my_git\n" +
"  abc = \"def\" true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
	}
	
	@Then("^the namespace compiles without error$")
	public void the_namespace_compiles_without_error() throws Exception {
	}
}
