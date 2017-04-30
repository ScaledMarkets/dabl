package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Main;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestTaskDependencies extends TestBase {
	
	DependencyGraph graph;
	
	@Given("^Task A has an output that is an input to B$")
	public void task_A_has_an_output_that_is_an_input_to_B() throws Throwable {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_git type \"git\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"files XYZ of \"my_repo\" in my_maven \n" +
"files QRS of \"qrs\" in my_git \n" +
"task Atask\n" +
"  inputs of \"repo1\" in my_git \"x\"\n" +
"  outputs Aoutput of \"repo2\" in my_git \"y\" \n" +
"task Btask\n" +
"  inputs Atask.Aoutput \n" +
"  outputs of \"repo2\" in my_git \"y\""
			);
		
		createHelper(Main.compile(false, true, false, true, reader));
		assertThat(getHelper().getState().getGlobalScope() != null);
	}
	
	@When("^I compile them in simulate mode$")
	public void i_compile_them_in_simulate_mode() throws Throwable {
		
		this.graph = DependencyGraph.genDependencySet(getHelper().getState());
	}
	
	@Then("^I can verify that B depends on A$")
	public void i_can_verify_that_B_depends_on_A() throws Throwable {
		
		AOtaskDeclaration taskADecl = getHelper().getTaskDeclaration("Atask");
		assertThat(taskADecl != null);
		AOtaskDeclaration taskBDecl = getHelper().getTaskDeclaration("Btask");
		assertThat(taskBDecl != null);
		Task taskA = this.graph.getTaskForDeclaration(taskADecl);
		assertThat(taskA != null);
		Task taskB = this.graph.getTaskForDeclaration(taskBDecl);
		assertThat(taskB != null);
		assertThat(taskB.getProducers().contains(taskA));
		assertThat(taskA.getConsumers().contains(taskB));
	}
}
