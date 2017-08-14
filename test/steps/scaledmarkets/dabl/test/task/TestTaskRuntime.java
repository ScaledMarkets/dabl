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

public class TestTaskRuntime extends TestBase {
	
	private String repoType;
	CompilerState state;
	private static String TaskName = "t123";

	// Scenario: Simple
	
	@Given("^Docker is running$")
	public void docker_is_running() throws Exception {
		Docker docker = Docker.connect();
		docker.ping();
		docker.close();
	}
	
	@When("^a task should execute$")
	public void a_task_should_execute() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"task " + TaskName + "\n" +
"  when true \n"
			);
		
		Dabl dabl = new Dabl(false, true, true, reader);
		this.state = dabl.process();
		createHelper(this.state);
	}
	
	@Then("^the task executes$")
	public void the_task_executes() throws Exception {
		
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();

		System.out.println("Creating Executor...");
		DefaultExecutor executor = new DefaultExecutor(this.state,
			taskContainerFactory, true);
		System.out.println("Executing executor...");
		executor.execute();
		System.out.println("...execution completed.");
		int status = executor.getTaskStatus(this.TaskName, true);
		System.out.println("Retrieved status...");
		assertThat(status == 0, "Task completed with status=" + status);
		System.out.println("Successful completion.");
	}
}
