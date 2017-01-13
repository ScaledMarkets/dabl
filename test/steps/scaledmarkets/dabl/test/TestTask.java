package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.main.Dabl;
import scaledmarkets.dabl.main.CompilerState;
import java.io.Reader;
import java.io.StringReader;
import scaledmarkets.dabl.node.*;
import java.util.LinkedList;

public class TestTask extends TestBase {
	
	Start ast;
	
	@When("^I compile a simple task$")
	public void i_compile_a_simple_task() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple task t123 when true inputs \"x\" from \"repo1\"  in my_git outputs \"y\" from \"repo2\" in my_git abc = \"def\" true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		CompilerState state = dabl.process();
		ast = state.getAST();
	}
	
	@Then("^I can retrieve the name of the task$")
	public void i_can_retrieve_the_name_of_the_task() throws Exception {
		AOnamespace namespace = (AOnamespace)ast.getPOnamespace();
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
	}
}
