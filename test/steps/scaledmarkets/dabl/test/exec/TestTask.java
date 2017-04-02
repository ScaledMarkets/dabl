package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestTask extends TestBase {
	
	@When("^I compile a simple task$")
	public void i_compile_a_simple_task() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"task t123\n" +
"  when true\n" +
"  inputs of \"repo1\" in my_git \"x\"\n" +
"  outputs of \"repo2\" in my_git \"y\"\n" +
"  abc = ff true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}
	
	@Then("^I can retrieve the the task by its name$")
	public void i_can_retrieve_the_the_task_by_its_name() throws Exception {
		
		SymbolEntry entry = getHelper().getNamespaceSymbolEntry("simple");
		
		// Get the namespace's symbol table.
		NameScopeEntry nse = (NameScopeEntry)entry;
		
		NameScope namespaceScope = nse.getOwnedScope();
		SymbolEntry taskEntry = namespaceScope.getEntry("t123");
		assertThat(taskEntry != null);
	}

	@Given("^I have two tasks and one has an output that is an input to the other$")
	public void i_have_two_tasks_and_one_has_an_output_that_is_an_input_to_the_other() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can verify the task dependency$")
	public void i_can_verify_the_task_dependency() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Given("^I have a task containing bash commands$")
	public void i_have_a_task_containing_bash_commands() throws Throwable {
		Reader reader = new StringReader(
"namespace simple \n" +
"task t123\n" +
"  when true\n" +
"  bash echo hello\n" +
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}
	
	@When("^I compile the task$")
	public void i_compile_the_task() throws Throwable {
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();
		Executor exec = new DefaultExecutor(state, taskContainerFactory, false);
		exec.execute();
	}
	
	@Then("^the bash commands are executed$")
	public void the_bash_commands_are_executed() throws Throwable {
	}
}
