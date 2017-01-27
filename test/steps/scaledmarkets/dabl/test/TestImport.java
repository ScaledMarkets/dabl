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

public class TestImport extends TestBase {
	
	@When("^I import another namespace$")
	public void i_import_another_namespace() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"  import another\n" +
"  files Stuff from \"myrepo\" in my_maven\n" +
"    include \"*.java\""
			);
		
		String[] namespaces = new String[] {
"namespace another\n" +
"  repo my_maven type \"maven\" path \"mymaven.somewhere.com\""
		};

		Dabl dabl = new Dabl(false, true, reader, new InMemoryImportHandler(namespaces));
		this.state = dabl.process();
		
	}
	
	@Then("^the elements of the namespace are accessible$")
	public void the_elements_of_the_namespace_are_accessible() throws Exception {
		
		// Starting from the simple namespace, retrieve the DeclaredEntry of my_maven.
		
		NameScopeEntry namespaceEntry = getNamespaceSymbolEntry("simple");
		
		DeclaredEntry stuffEntry = getDeclaredEntry(namespaceEntry, "Stuff");
		Node n = stuffEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration);
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		TId reposName = filesDecl.getRepository();
		Object obj = state.getIn(reposName);
		assertThat(obj != null);
		assertThat(obj instanceof DeclaredEntry);
		DeclaredEntry reposEntry = (DeclaredEntry)obj;
		
		assertThat(reposEntry.getName().equals("my_maven"));
		n = reposEntry.getDefiningNode();
		assertThat(n instanceof AOrepoDecl);
		
		Node p = n.parent();
		assertThat(p instanceof POnamespaceElt);
		
		assertThat(p.parent() instanceof AOnamespace);
	}
}
