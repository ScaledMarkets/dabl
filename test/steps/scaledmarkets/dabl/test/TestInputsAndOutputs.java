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

public class TestInputsAndOutputs extends TestBase {
	
	CompilerState state;
	
	@When("^I compile a task that has inputs and outputs$")
	public void i_compile_a_task_that_has_inputs_and_outputs() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple" +
"  task t123\n" +
"    inputs MyInputs "\"java/*.java\" from \"myrepo\" in MyRepository\n" +
"    outputs MyOutputs "\"classes\*.class\" from \"myrepo\" in MyRepository\n"
			);
		
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
		
	}
	
	@Then("^the inputs and outputs are retrievable$")
	public void the_inputs_and_outputs_are_retrievable() throws Exception {
		
		
		assertThat(false);
	}
}
