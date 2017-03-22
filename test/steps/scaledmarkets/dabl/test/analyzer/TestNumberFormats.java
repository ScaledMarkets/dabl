package scaledmarkets.dabl.test.analyzer;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.node.*;

import java.io.Reader;
import java.io.StringReader;
import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class TestNumberFormats extends TestBase {
	
	Reader reader;

	@Before
	public void beforeEachScenario() throws Exception {
		reader = null;
	}
	

	// Test whole number
	
	@When("^a whole number is processed$")
	public void a_whole_number_is_processed() throws Throwable {

		Reader reader = new StringReader(
"namespace simple\n" +
"  task t123\n" +
"    when 1 < 2"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
	}
	
	@Then("^I can retrieve the numeric value$")
	public void i_can_retrieve_the_numeric_value() throws Throwable {
		
		NameScopeEntry namespaceEntry = getHelper().getNamespaceSymbolEntry("simple");
		DeclaredEntry repoEntry = getHelper().getDeclaredEntry(namespaceEntry, "t123");
		Node n = repoEntry.getDefiningNode();
		assertThat(n instanceof AOtaskDeclaration, () -> {
			System.out.println("\tn is a " + n.getClass().getName());
		});
		AOtaskDeclaration taskDecl = (AOtaskDeclaration)n;
		
		LinkedList<POexpr> whens = taskDecl.getWhen();
		assertThat(whens.size() == 1);
		POexpr when = whens.get(0);
		assertThat(when instanceof ABinaryOexpr);
		ABinaryOexpr binExpr = (ABinaryOexpr)when;
		
		POexpr op1 = binExpr.getOperand1();
		assertThat(op1 instanceof ALiteralOexpr);
		ALiteralOexpr lit = (ALiteralOexpr)op1;
		
		POliteral o = lit.getOliteral();
		assertThat(o instanceof ANumericOliteral);
		ANumericOliteral numlit = (ANumericOliteral)o;
		
		POnumericLiteral nl = numlit.getOnumericLiteral();
		assertThat(nl instanceof AIntOnumericLiteral);
		AIntOnumericLiteral intlit = (AIntOnumericLiteral)nl;
		
		POsign ps = intlit.getOsign();
		assertThat(ps instanceof APositiveOsign);
		APositiveOsign psign = (APositiveOsign)ps;
		
		TWholeNumber wn = intlit.getWholeNumber();
		assertThat(wn.getText().equals("1"));
	}
	
	// Test negative whole number

	@When("^a negative whole number is processed$")
	public void a_negative_whole_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test decimal number
	
	@When("^a decimal number is processed$")
	public void a_decimal_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test negative decimal number

	@When("^a negative decimal number is processed$")
	public void a_negative_decimal_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test simple numeric expression

	@When("^a simple numeric expression is processed$")
	public void a_simple_numeric_expression_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can retrieve the values and operators$")
	public void i_can_retrieve_the_values_and_operators() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test simple numeric expression with a negative number

	@When("^a simple numeric expression with a negative number is processed$")
	public void a_simple_numeric_expression_with_a_negative_number_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test numeric pattern with two decmial points

	@When("^a numeric pattern with two decmial points is processed$")
	public void a_numeric_pattern_with_two_decmial_points_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^I can retrieve the pattern components$")
	public void i_can_retrieve_the_pattern_components() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	// Test numeric pattern with a wildcard

	@When("^a numeric pattern with a wildcard is processed$")
	public void a_numeric_pattern_with_a_wildcard_is_processed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
}
