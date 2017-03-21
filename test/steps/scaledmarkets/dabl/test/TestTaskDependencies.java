package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;

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
"  userid \"GitUserId\" password \"GitPassword\"\n" +
"files XYZ of \"my_repo\" in my_maven\n" +
"files QRS of \"qrs\" in my_git\n" +
"task A\n" +
"  inputs \"x\" of \"repo1\" in my_git\n" +
"  outputs \"y\" of \"repo2\" in my_git\n" +
"task B\n" +
"  inputs \"x\" of \"repo1\" in my_git\n" +
"  outputs \"y\" of \"repo2\" in my_git"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
		throw new Exception();
	}
	
	@When("^I compile them in simulate mode$")
	public void i_compile_them_in_simulate_mode() throws Throwable {
		
		this.graph = DependencyGraph.genDependencySet(getHelper().getState());
	}
	
	@Then("^I can verify that B depends on A$")
	public void i_can_verify_that_B_depends_on_A() throws Throwable {
		
		AOtaskDeclaration taskADecl = getHelper().getTaskDeclaration("A");
		assertThat(taskADecl != null);
		AOtaskDeclaration taskBDecl = getHelper().getTaskDeclaration("B");
		assertThat(taskBDecl != null);
		Task taskA = this.graph.getTaskForDeclaration(taskADecl);
		assertThat(taskA != null);
		Task taskB = this.graph.getTaskForDeclaration(taskBDecl);
		assertThat(taskB != null);
		assertThat(taskB.getProducers().contains(taskA));
		
		throw new Exception();
	}
}
