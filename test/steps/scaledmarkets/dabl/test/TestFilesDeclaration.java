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
import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class TestFilesDeclaration extends TestBase {
	
	CompilerState state;
	
	@When("^I declare a file set$")
	public void i_declare_a_file_set() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple\n" +
"  repo my_maven type \"maven\"\n" +
"    path \"mymaven.abc.com\"\n" +
"    userid \"MavenUserId\" password \"MavenPassword\"\n" +
"  files Stuff from \"myrepo\" in my_maven\n" +
"    include \"*.java\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		this.state = dabl.process();
	}
	
	@Then("^I can reference the file set elsewhere$")
	public void i_can_reference_the_file_set_elsewhere() throws Exception {
		
		NameScopeEntry entry = getNamespaceSymbolEntry("simple");
		NameScope scope = entry.getOwnedScope();
		SymbolEntry e = scope.getEntry("Stuff");
		assertThat(e != null);
		assertThat(e instanceof DeclaredEntry);
		DeclaredEntry filesEntry = (DeclaredEntry)e;
		Node n = filesEntry.getDefiningNode();
		assertThat(n instanceof AOfilesDeclaration);
	}
}
