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

public class TestStringExpression extends TestBase {
	
	@When("^I compile a static string expression$")
	public void i_compile_a_static_string_expression() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_git type \"git\"\n" +
"  scheme \"https\"\n" +
"  path \"github.com/myteam\"\n" +
"  userid \"johnsmith\" password \"$\" ^ \"MyPassword\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
	}
	
	@Then("^the expression value can be retrieved and is correct$")
	public void the_expression_value_can_be_retrieved_and_is_correct() throws Exception {
		
		NameScopeEntry namespaceEntry = getHelper().getNamespaceSymbolEntry("simple");
		DeclaredEntry repoEntry = getHelper().getDeclaredEntry(namespaceEntry, "my_git");
		Node n = repoEntry.getDefiningNode();
		assertThat(n instanceof AOrepoDeclaration, () -> {
			System.out.println("\tn is a " + n.getClass().getName());
		});
		AOrepoDeclaration repoDec = (AOrepoDeclaration)n;
		
		POstringValueOpt p = repoDec.getPassword();
		assertThat(p != null);
		assertThat(p instanceof ASpecifiedOstringValueOpt);
		ASpecifiedOstringValueOpt pswd = (ASpecifiedOstringValueOpt)p;
		
		/*
		String value = getStringLiteralValue(pswd.getOstringLiteral());
		assertThat(value.equals("$MyPassword"));
		*/
	}

	@When("^a multi-line string is processed$")
	public void a_multi_line_string_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^the full string value is obtainable from the AST$")
	public void the_full_string_value_is_obtainable_from_the_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@When("^a triple-quote string is used$")
	public void a_triple_quote_string_is_used() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^one can obtain the full string value from the AST$")
	public void one_can_obtain_the_full_string_value_from_the_AST() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}

}
