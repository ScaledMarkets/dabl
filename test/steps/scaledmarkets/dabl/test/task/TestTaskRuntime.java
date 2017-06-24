package scaledmarkets.dabl.test.task;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;

import scaledmarkets.dabl.analyzer.*;
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
	
	@When("^a task should execute$")
	public void a_task_should_execute() throws Exception {
		
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
		this.state = dabl.process();
		createHelper(this.state);
	}
	
	@Then("^the task executes$")
	public void the_task_executes() throws Exception {
		
		TaskContainerFactory taskContainerFactory = new TaskDockerContainerFactory();

		DefaultExecutor executor = new DefaultExecutor(this.state,
			taskContainerFactory, true);
		int status = executor.getTaskStatus(this.TaskName, true);
		assertThat(status == 0, "Task completed with status=" + status);
	}
}
