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
		
		NameScopeEntry simpleNamespaceEntry = getNamespaceSymbolEntry("simple");
		
		DeclaredEntry stuffEntry = getDeclaredEntry(simpleNamespaceEntry, "Stuff");
		Node n = stuffEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration);
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		POidRef p = filesDecl.getRepository();
		assertThat(p instanceof AOidRef);
		AOidRef reposRef = (AOidRef)p;
		TId reposId = reposRef.getId();
		assertThat(reposId.getText().equals("my_maven"));
		
		Object o = state.out.get(reposId);
		assertThat(o != null);
		It is null because the Id is in a different CompilerState - the one
		created by the separate Dabl instance that was created to
		compile the imported namespace.
		
		
		
		
		assertThat(o instanceof DeclaredEntry, "o is a " + o.getClass().getName());
		DeclaredEntry reposEntry = (DeclaredEntry)o;
		assertThat(reposEntry.getName().equals("my_maven"));
		assertThat(reposEntry.getDefiningNode() instanceof AOrepoDecl);
	}
}
