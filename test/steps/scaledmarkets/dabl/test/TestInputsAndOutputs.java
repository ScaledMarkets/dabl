package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.main.*;
import scaledmarkets.dabl.node.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.LinkedList;

public class TestInputsAndOutputs extends TestBase {
	
	@When("^I compile a task that has inputs and outputs$")
	public void i_compile_a_task_that_has_inputs_and_outputs() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple\n" +
"  task t123\n" +
"    inputs MyInputs \"abc.jar\" from \"myrepo\" in MyRepository, \n" +
"      \"java/*.java\" from \"myrepo\" in MyRepository\n" +
"    outputs MyOutputs \"classes/*.class\" from \"myrepo\" in MyRepository"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
		
	}
	
	@Then("^the inputs and outputs are retrievable$")
	public void the_inputs_and_outputs_are_retrievable() throws Exception {
		
		NameScopeEntry namespaceEntry = getNamespaceSymbolEntry("simple");
		DeclaredEntry de = getDeclaredEntry(namespaceEntry, "t123");
		assertThat(de instanceof NameScopeEntry);
		NameScopeEntry taskEntry = (NameScopeEntry)de;
		
		Node n = taskEntry.getDefiningNode();
		assertThat(n instanceof AOtaskDeclaration);
		AOtaskDeclaration taskDecl = (AOtaskDeclaration)n;
		
		NameScope taskScope = taskEntry.getOwnedScope();
		SymbolTable taskSymbolTable = taskScope.getSymbolTable();
		
		// Look up inputs and outputs.
		SymbolEntry inputsSymbolEntry = taskSymbolTable.getEntry("MyInputs");
		assertThat(inputsSymbolEntry != null);
		
		SymbolEntry outputsSymbolEntry = taskSymbolTable.getEntry("MyOutputs");
		assertThat(outputsSymbolEntry != null);
	}
}
