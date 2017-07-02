package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;

import scaledmarkets.dabl.Executor;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;
import scaledmarkets.dabl.repos.DummyProvider;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestTask extends TestBase {
	
	private String repoType;
	private static String TaskName = "t123";

	// Scenario: Simple
	
	@When("^I compile a simple task$")
	public void i_compile_a_simple_task() throws Exception {
		
		this.repoType = DummyProvider.RepoType;
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "\n" +
"  when true\n" +
"  inputs of \"project1\" in my_git include \"x\"\n" +
"  outputs of \"project2\" in my_git include \"y\"\n" +
"  abc = ff true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null, "global scope is null");
	}
	
	@Then("^I can retrieve the the task by its name$")
	public void i_can_retrieve_the_the_task_by_its_name() throws Exception {
		
		SymbolEntry entry = getHelper().getNamespaceSymbolEntry("simple");
		
		// Get the namespace's symbol table.
		NameScopeEntry nse = (NameScopeEntry)entry;
		
		NameScope namespaceScope = nse.getOwnedScope();
		SymbolEntry taskEntry = namespaceScope.getEntry(TaskName);
		assertThat(taskEntry != null, "task entry is null");
	}
	
	
	// Scenario: Two tasks

	@Given("^I have two tasks and one has an output that is an input to the other$")
	public void i_have_two_tasks_and_one_has_an_output_that_is_an_input_to_the_other() throws Throwable {
		
		this.repoType = DummyProvider.RepoType;
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "A \n" +
"  when true\n" +
"  outputs of \"project1\" in my_git \"y\"\n" +
"task " + TaskName + "B \n" +
"  when true\n" +
"  inputs of \"project2\" in my_git \"x\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null, "global scope is null");
		
		throw new Exception();
	}
	
	@Then("^I can verify the task dependency$")
	public void i_can_verify_the_task_dependency() throws Throwable {
		
		
		throw new Exception();
	}
	
	
	// Scenario: Execute bash
	
	@Given("^I have a task containing bash commands$")
	public void i_have_a_task_containing_bash_commands() throws Throwable {
		Reader reader = new StringReader(
"namespace simple \n" +
"task " + TaskName + "\n" +
"  when true\n" +
"  bash echo hello\n"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null, "global scope is null");
	}
	
	@When("^I compile the task$")
	public void i_compile_the_task() throws Throwable {
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();
		Executor exec = new DefaultExecutor(getHelper().getState(), taskContainerFactory, false);
		exec.execute();
	}
	
	@Then("^the bash commands are executed$")
	public void the_bash_commands_are_executed() throws Throwable {
	}
	
	
	// Scenario: Basic pushing inputs and retrieving outputs
	
	@Given("^a task containing bash commands that compute the sum of values in an input file$")
	public void a_task_containing_bash_commands_that_compute_the_sum_of_values_in_an_input_file() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^the outputs contains a file with the correct sum value$")
	public void the_outputs_contains_a_file_with_the_correct_sum_value() throws Throwable {
		
		throw new Exception();
	}
	
	
	// Scenario: Complex pushing inputs and retrieving outputs
	
	@Given("^a task containing bash commands that read three files that are named in the inputs and two other files that are referenced via a wildcard$")
	public void complex_scenario() throws Throwable {
		
		throw new Exception();
	}

	@And("^the task writes two files by name and two more by wildcard$")
	public void the_task_writes_two_files_by_name_and_two_more_by_wildcard() throws Throwable {
		
		throw new Exception();
	}
	
	@And("^the task modifies one of the input files$")
	public void the_task_modifies_one_of_the_input_files() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^the outputs contain the output files, and the input files are unmodified$")
	public void the_outputs_contain_the_output_files_and_the_input_files_are_unmodified() throws Throwable {
		
		throw new Exception();
	}
}
