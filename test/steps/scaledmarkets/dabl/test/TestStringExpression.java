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

public class TestStringExpression extends TestBase {
	
	CompilerState state;
	
	@When("^I compile a static string expression$")
	public void i_compile_a_static_string_expression() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple" +
"repo my_git type git\n" +
"  scheme \"https\"\n" +
"  path \"github.com/myteam\"\n" +
"  userid \"johnsmith\" password \"$\" ^ \"MyPassword\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
	}
	
	@Then("^the expression value can be retrieved and is correct$")
	public void the_expression_value_can_be_retrieved_and_is_correct() throws Exception {
		
		
		assertThat(false);
	}
}
