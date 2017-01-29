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

public class TestFunctionDeclaration extends TestBase {
	
	@When("^I declare a function$")
	public void i_declare_a_function() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple\n" +
"  function f1 int, string \n" +
"    binds to \"java\" method \"convertToString\"\n" +
"    returns string"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
		
	}
	
	@Then("^I can obtain meta information about the function$")
	public void i_can_obtain_meta_information_about_the_function() throws Exception {
		
		// Get the function name and arg types.
		NameScopeEntry nameScopeEntry = getNamespaceSymbolEntry("simple");
		DeclaredEntry functionEntry = getDeclaredEntry(nameScopeEntry, "f1");
		Node n = functionEntry.getDefiningNode();
		assertThat(n instanceof AOfunctionDeclaration);
		AOfunctionDeclaration funcDecl = (AOfunctionDeclaration)n;
		
		TId id = funcDecl.getName();
		LinkedList<POtypeSpec> argTypes = funcDecl.getOtypeSpec();
		POstringLiteral targetLangNode = funcDecl.getTargetLanguage();
		POstringLiteral targetNameNode = funcDecl.getTargetName();
		POtypeSpec returnType = funcDecl.getReturnType();
		
		assertThat(id.getText().equals("f1"));
		assertThat(argTypes.size() == 3, () -> { // Note: the second arg is a separator
			System.out.println("argTypes.size() == " + argTypes.size());
			for (POtypeSpec t : argTypes) {
				System.out.println("\targ type = " + t.getClass().getName());
			}
		});
		
		int index = 0;
		for (POtypeSpec typeSpec : argTypes) {
			switch (index) {
				case 0: assertThat(typeSpec instanceof AIntOtypeSpec); break;
				case 1: assertThat(typeSpec instanceof AStringOtypeSpec); break;
				default: assertThat(false);
			}
		}
		
		String targetLang = getStringLiteralValue(targetLangNode);
		assertThat(targetLang.equals("java"));
		String targetName = getStringLiteralValue(targetNameNode);
		assertThat(targetName.equals("convertToString"));
		
		assertThat(returnType instanceof AStringOtypeSpec);
	}
}
