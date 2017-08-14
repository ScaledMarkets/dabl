package scaledmarkets.dabl.test.analyzer;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.client.*;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import scaledmarkets.dabl.test.TestBase;

import java.io.Reader;
import java.io.StringReader;
import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class TestForwardReferences extends TestBase {
	
	Reader reader;
	
	@When("^I declare a symbol after it is referenced$")
	public void i_declare_a_symbol_after_it_is_referenced() throws Throwable {
		
		this.reader = new StringReader(
"namespace simple\n" +
"  files Stuff of \"myrepo\" in my_maven\n" +
"    \"*.java\"" +
"  repo my_maven type \"maven\"\n" +
"    path \"mymaven.abc.com\"\n" +
"    userid \"MavenUserId\" password \"MavenPassword\"\n"
			);
	}
	
	@Then("^the IdRefAnnotation is correct$")
	public void the_IdRefAnnotation_is_correct() throws Throwable {
		
		Dabl dabl = new Dabl(false, true, true, this.reader);
		createHelper(dabl.process());
		
		// Find the Id that references my_repo from the files declaration.
		
		Node n = getHelper().getDeclaration("Stuff");
		assertThat(n instanceof AOfilesDeclaration, "n is not a AOfilesDeclaration");
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		//POidRef p = filesDecl.getRepository();
		POartifactSet pas = filesDecl.getOartifactSet();
		assertThat(pas instanceof ARemoteOartifactSet, "pas is a " + pas.getClass().getName());
		ARemoteOartifactSet as = (ARemoteOartifactSet)pas;
		POidRef p = as.getRepositoryId();
		assertThat(p instanceof AOidRef, "p is not a AOidRef");
		AOidRef idRef = (AOidRef)p;
		
		String path = Utilities.createNameFromPath(idRef.getId());
		assertThat(path.equals("my_maven"), "id is not 'my_maven'");
		Annotation annot = getHelper().getState().getOut(idRef);
		assertThat(annot != null, "annot is null");
		assertThat(annot instanceof IdRefAnnotation, "annot is not a IdRefAnnotation");
		IdRefAnnotation idRefAnnot = (IdRefAnnotation)annot;
		SymbolEntry entry = idRefAnnot.getDefiningSymbolEntry();
		assertThat(entry != null, "entry is null");
		assertThat(entry instanceof DeclaredEntry, "entry is not a DeclaredEntry");
		DeclaredEntry declEntry = (DeclaredEntry)entry;
		
		n = declEntry.getDefiningNode();
		assertThat(n != null, "n is null");
		assertThat(n instanceof AOrepoDeclaration, "n is not a AOrepoDeclaration");
		AOrepoDeclaration repoDeclaration = (AOrepoDeclaration)n;
	}
}
