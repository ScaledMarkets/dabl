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

public class TestImport extends TestBase {
	
	@When("^I import another namespace$")
	public void i_import_another_namespace() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"  import another\n" +
"  files Stuff from \"myrepo\" in my_maven\n" +
"    include \"*.java\""
			);
		
		Map<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("another",
"namespace another\n" +
"  repo my_maven type \"maven\" path \"mymaven.somewhere.com\""
		);

		Dabl dabl = new Dabl(false, true, reader, new InMemoryImportHandler(namespaces));
		createHelper(dabl.process());
		
	}
	
	@Then("^the elements of the namespace are accessible$")
	public void the_elements_of_the_namespace_are_accessible() throws Exception {
		
		// Starting from the simple namespace, retrieve the DeclaredEntry of my_maven.
		
		NameScopeEntry simpleNamespaceEntry = getHelper().getNamespaceSymbolEntry("simple");
		
		DeclaredEntry stuffEntry = getHelper().getDeclaredEntry(simpleNamespaceEntry, "Stuff");
		Node n = stuffEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration);
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		POidRef p = filesDecl.getRepository();
		assertThat(p instanceof AOidRef);
		AOidRef reposRef = (AOidRef)p;
		TId reposId = reposRef.getId();
		assertThat(reposId.getText().equals("my_maven"));
		
		Annotation a = getHelper().getState().out.get(reposRef);
		assertThat(a != null, "It appears that the local reference to my_maven was not resolved");
		assertThat(a instanceof IdRefAnnotation, "a is a " + a.getClass().getName());
		IdRefAnnotation idrefan = (IdRefAnnotation)a;
		SymbolEntry e = idrefan.getDefiningSymbolEntry();
		assertThat(e instanceof DeclaredEntry);
		DeclaredEntry reposEntry = (DeclaredEntry)e;
		assertThat(reposEntry.getName().equals("my_maven"));
		assertThat(reposEntry.getDefiningNode() instanceof AOrepoDecl);
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
