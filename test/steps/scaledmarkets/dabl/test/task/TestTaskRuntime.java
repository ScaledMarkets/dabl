package scaledmarkets.dabl.test.task;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;

import scaledmarkets.dabl.client.*;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.docker.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.task.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.io.PrintWriter;

public class TestTaskRuntime extends TestBase {
	
	private Reader reader;
	private String repoType;
	CompilerState state;
	private static String Task1Name = "task1";

	// Scenario: Simple
	
	@Given("^a trivial task$")
	public void a_trivial_task() throws Exception {
		
		this.reader = new StringReader(
"namespace simple \n" +
"task " + Task1Name + "\n" +
"  when true \n"
			);
	}
	
	@When("^the namespace is processed$")
	public void the_namespace_is_processed() throws Exception {
		
		Dabl dabl = new Dabl(false, true, true, this.reader);
		this.state = dabl.process();
		createHelper(this.state);
	}
	
	@Then("^the task executes$")
	public void the_task_executes() throws Exception {
		
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();

		System.out.println("Creating Executor...");
		Set<String> keepSet = new HashSet<String>();
		keepSet.add(Task1Name);
		DefaultExecutor executor = new DefaultExecutor(this.state,
			taskContainerFactory, true, true, keepSet);
		System.out.println("Executing executor...");
		executor.execute();
		System.out.println("...execution completed.");
		int status = executor.getTaskStatus(this.Task1Name, false);
		System.out.println("Retrieved status...");
		assertThat(status == 0, "Task completed with status=" + status);
		System.out.println("Successful completion.");
	}

	// Scenario: Test that a true When condition causes task to execute
	
	@Given("^a task with a true when condition$")
	public void a_task_with_a_true_when_condition() throws Exception {
		
		this.reader = new StringReader(
"namespace simple \n" +
"task " + Task1Name + "\n" +
"  when true \n"
			);
	}

	
	// Scenario: Test that a false When condition prevents task from executing
	
	@Given("^a task with a false when condition$")
	public void a_task_with_a_false_when_condition() throws Exception {
		
		this.reader = new StringReader(
"namespace simple \n" +
"task " + Task1Name + "\n" +
"  when false \n"
			);
	}

	@Then("^the task does not execute$")
	public void the_task_does_not_execute() throws Exception {
		
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();

		System.out.println("Creating Executor...");
		Set<String> keepSet = new HashSet<String>();
		keepSet.add(Task1Name);
		DefaultExecutor executor = new DefaultExecutor(this.state,
			taskContainerFactory, true, true, keepSet);
		System.out.println("Executing executor...");
		executor.execute();
		System.out.println("...execution completed.");
		try {
			executor.getTaskStatus(this.Task1Name, false);
			assertThat(false, "Task executed, but it should not have");
		} catch (Exception ex) {
		}
		System.out.println("Successful completion.");
	}

	
	// Scenario: Test that absence of a When condition behaves in the default manner
	
	@Given("^a task without a when condition and inputs that are newer than the outputs$")
	public void a_task_without_a_when_condition_and_inputs_that_are_newer_than_the_outputs() throws Exception {
		
		File x = new File("x.txt");
		File y = new File("y.txt");
		PrintWriter xw = new PrintWriter(x);
		PrintWriter yw = new PrintWriter(y);
		xw.println();
		yw.println();
		xw.flush();
		yw.flush();
		xw.close();
		yw.close();
		this.reader = new StringReader(
"namespace simple \n" +
"task " + Task1Name + "\n" +
"  inputs \"y.txt\" \n" +
"  outputs \"x.txt\" \n"  // x.txt is older than y.txt
			);
	}
}
