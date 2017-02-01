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
		
		NameScopeEntry namespaceEntry = getNamespaceSymbolEntry("simple");
		
		DeclaredEntry stuffEntry = getDeclaredEntry(namespaceEntry, "Stuff");
		Node n = stuffEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration);
		AOfilesDeclaration filesDecl = (AOfilesDeclaration)n;
		
		TId reposName = filesDecl.getRepository();
		assertThat(reposName.getText().equals("my_maven"));
		
		// Obtain the declaration to which the TId is bound.
		....
		
		NameScope simpleNameScope = namespaceEntry.getOwnedScope();
		SymbolTable simpleSymbolTable = simpleNameScope.getSymbolTable();
		
		
		
		assertThat(e != null, () -> {
			sablecc.PrettyPrint.pp(state.ast);
			
			NameScope globalScope = state.globalScope;
			SymbolTable globalSymbolTable = globalScope.getSymbolTable();
			SymbolTable nextTable = globalSymbolTable.getNextTable();
			if (nextTable == null) throw new RuntimeException(
				"nextTable is null");
			if (! nextTable.getName().equals("another")) throw new RuntimeException(
				"nextTable name is " + nextTable.getName());
		});
		assertThat(e instanceof DeclaredEntry);
		DeclaredEntry entry = (DeclaredEntry)e;
		
		// Check that the defining node is a repo declaration.
		n = entry.getDefiningNode();
		assertThat(n instanceof AOrepoDecl);
		
		// Check that the repo declaration occurs within a namespace.
		Node p = n.parent();
		assertThat(p instanceof POnamespaceElt);
		assertThat(p.parent() instanceof AOnamespace);
	}
}
