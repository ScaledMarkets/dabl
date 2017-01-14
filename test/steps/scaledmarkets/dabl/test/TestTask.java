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

public class TestTask extends TestBase {
	
	CompilerState state;
	
	@When("^I compile a simple task$")
	public void i_compile_a_simple_task() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"task t123\n" +
"  when true\n" +
"  inputs \"x\" from \"repo1\" in my_git\n" +
"  outputs \"y\" from \"repo2\" in my_git\n" +
"  abc = \"def\" true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
	}
	
	@Then("^I can retrieve the the task by its name$")
	public void i_can_retrieve_the_name_of_the_task() throws Exception {
		
		List<NameScope> scopeStack = this.state.scopeStack;
		SymbolEntry entry = this.state.scopeStack.get(0).getSymbolTable().getEntry("t123");
		assertThat(entry != null);
		
		
		/*
		AOnamespace namespace = (AOnamespace)(this.state.ast.getPOnamespace());
		LinkedList<POnamespaceElt> elts = namespace.getOnamespaceElt();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof ATaskOnamespaceElt) {
				ATaskOnamespaceElt taskElt = (ATaskOnamespaceElt)elt;
				AOtaskDeclaration taskDecl = (AOtaskDeclaration)taskElt.getOtaskDeclaration();
				TId id = taskDecl.getName();
				String taskName = id.getText();
				if (taskName.equals("t123")) {
					return;
				}
			}
		}
		throw new Exception("Did not find task t123");
		*/
	}
}
