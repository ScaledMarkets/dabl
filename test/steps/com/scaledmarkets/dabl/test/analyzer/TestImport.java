package com.scaledmarkets.dabl.test.analyzer;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.scaledmarkets.dabl.client.*;
import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.util.Utilities;
import com.scaledmarkets.dabl.test.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class TestImport extends TestBase {
	
	@When("^I import another namespace$")
	public void i_import_another_namespace() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"  import another\n" +
"  files Stuff of \"myrepo\" in my_maven\n" +
"    include \"*.java\""
			);
		
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("another",
"namespace another\n" +
"  public repo my_maven type \"maven\" path \"mymaven.somewhere.com\""
		);

		Dabl dabl = new Dabl(false, true, true, reader);
		
		dabl.process(new DablAnalyzerFactory() {
			public ImportHandler createImportHandler() {
				return new InMemoryImportHandler(new HashMap<String, String>());
			}
		});
		
		createHelper(dabl.process());
		
	}
	
	@Then("^the elements of the namespace are accessible$")
	public void the_elements_of_the_namespace_are_accessible() throws Exception {
		
		// Starting from the simple namespace, retrieve the DeclaredEntry of my_maven.
		
		NameScopeEntry simpleNamespaceEntry = getHelper().getNamespaceSymbolEntry("simple");
		
		DeclaredEntry stuffEntry = getHelper().getDeclaredEntry(simpleNamespaceEntry, "Stuff");
		Node n = stuffEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration, "n is not a AOfilesDeclaration");
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		POartifactSet pas = filesDecl.getOartifactSet();
		assertThat(pas instanceof ARemoteOartifactSet, "pas is a " + pas.getClass().getName());
		ARemoteOartifactSet as = (ARemoteOartifactSet)pas;
		POidRef p = as.getRepositoryId();
		assertThat(p instanceof AOidRef, "p is not a AOidRef");
		AOidRef reposRef = (AOidRef)p;
		List<TId> reposIds = reposRef.getId();
		String path = Utilities.createNameFromPath(reposIds);
		assertThat(path.equals("my_maven"), "reposId is not 'my_maven'");
		
		Annotation a = getHelper().getState().getOut(reposRef);
		assertThat(a != null, "It appears that the local reference to my_maven was not resolved");
		assertThat(a instanceof IdRefAnnotation, "a is a " + a.getClass().getName());
		IdRefAnnotation idrefan = (IdRefAnnotation)a;
		SymbolEntry e = idrefan.getDefiningSymbolEntry();
		assertThat(e instanceof DeclaredEntry, "e is not a DeclaredEntry");
		DeclaredEntry reposEntry = (DeclaredEntry)e;
		assertThat(reposEntry.getName().equals("my_maven"), "repos entry name is not 'my_maven'");
		assertThat(reposEntry.getDefiningNode() instanceof AOrepoDeclaration,
			"reposEntry is not a AOrepoDeclaration");
	}

	@When("^I import a multi-level namespace$")
	public void i_import_a_multi_level_namespace() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@When("^a namespace imports a second namespace, and that imports a third, which in turn imports the first$")
	public void a_namespace_imports_a_second_namespace_and_that_imports_a_third_which_in_turn_imports_the_first() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}

}
