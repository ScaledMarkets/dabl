package scaledmarkets.dabl.test;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import sablecc.*;
import scaledmarkets.dabl.main.*;
import scaledmarkets.dabl.node.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;

public class TestHelpers extends TestBase {


	@When("^I call getPrimaryNamespace$")
	public void i_call_getPrimaryNamespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the correct AOnamespace symbol$")
	public void it_returns_the_correct_AOnamespace_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getNamespace with a start symbol$")
	public void i_call_getNamespace_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOnamespace symbol for that AST$")
	public void it_returns_the_AOnamespace_symbol_for_that_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getNamespaceFullName with a namespace argument$")
	public void i_call_getNamespaceFullName_with_a_namespace_argument() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the fully qualified name of that namespace$")
	public void it_returns_the_fully_qualified_name_of_that_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getNamespaceElements$")
	public void i_call_getNamespaceElements() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the POnamespaceElt elements of the namespace$")
	public void it_returns_the_POnamespaceElt_elements_of_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getNamespaceElements with a start symbol$")
	public void i_call_getNamespaceElements_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the POnamespaceElt elements for that AST$")
	public void it_returns_the_POnamespaceElt_elements_for_that_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getImportedNamespaces$")
	public void i_call_getImportedNamespaces() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AImportOnamespaceElt elements for the primary namespace$")
	public void it_returns_the_AImportOnamespaceElt_elements_for_the_primary_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getImportedNamespaces with a start symbol$")
	public void i_call_getImportedNamespaces_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AImportOnamespaceElt elements for the specified AST$")
	public void it_returns_the_AImportOnamespaceElt_elements_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getArtifactDeclarations$")
	public void i_call_getArtifactDeclarations() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOartifactDeclarations for the namespace$")
	public void it_returns_the_AOartifactDeclarations_for_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getArtifactDeclarations with a start symbol$")
	public void i_call_getArtifactDeclarations_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOartifactDeclarations for the specified AST$")
	public void it_returns_the_AOartifactDeclarations_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getRepoDeclarations$")
	public void i_call_getRepoDeclarations() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOrepoDeclarations for the namespace$")
	public void it_returns_the_AOrepoDeclarations_for_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getRepoDeclarations with a start symbol$")
	public void i_call_getRepoDeclarations_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOrepoDeclarations for the specified AST$")
	public void it_returns_the_AOrepoDeclarations_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getFilesDeclarations$")
	public void i_call_getFilesDeclarations() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOfilesDeclarations for the namespace$")
	public void it_returns_the_AOfilesDeclarations_for_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getFilesDeclarations with a start symbol$")
	public void i_call_getFilesDeclarations_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOfilesDeclarations for the specified AST$")
	public void it_returns_the_AOfilesDeclarations_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getFunctionDeclarations$")
	public void i_call_getFunctionDeclarations() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOfunctionDeclarations for the namespace$")
	public void it_returns_the_AOfunctionDeclarations_for_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getFunctionDeclarations with a start symbol$")
	public void i_call_getFunctionDeclarations_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOfunctionDeclarations for the specified AST$")
	public void it_returns_the_AOfunctionDeclarations_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getTaskDeclarations$")
	public void i_call_getTaskDeclarations() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOtaskDeclarations for the namespace$")
	public void it_returns_the_AOtaskDeclarations_for_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getTaskDeclarations with a start symbol$")
	public void i_call_getTaskDeclarations_with_a_start_symbol() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the AOtaskDeclarations for the specified AST$")
	public void it_returns_the_AOtaskDeclarations_for_the_specified_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getPrimaryNamespaceSymbolEntry$")
	public void i_call_getPrimaryNamespaceSymbolEntry() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the NameScopeEntry for the primary namespace$")
	public void it_returns_the_NameScopeEntry_for_the_primary_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getNamespaceSymbolEntry$")
	public void i_call_getNamespaceSymbolEntry() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the NameScopeEntry for the specified namespace$")
	public void it_returns_the_NameScopeEntry_for_the_specified_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getDeclaredEntry$")
	public void i_call_getDeclaredEntry() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the DeclaredEntry for the specified name, within the specified name scope$")
	public void it_returns_the_DeclaredEntry_for_the_specified_name_within_the_specified_name_scope() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the DeclaredEntry for the specified name within the top level of the namespace$")
	public void it_returns_the_DeclaredEntry_for_the_specified_name_within_the_top_level_of_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getDeclaration$")
	public void i_call_getDeclaration() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the declaring Node for the specified name at the top level of the namespace$")
	public void it_returns_the_declaring_Node_for_the_specified_name_at_the_top_level_of_the_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@When("^I call getArtifactDeclaration$")
	public void i_call_getArtifactDeclaration() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}
	
	@Then("^it returns the artifact declaration with the specified name$")
	public void it_returns_the_artifact_declaration_with_the_specified_name() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new PendingException();
	}

}
