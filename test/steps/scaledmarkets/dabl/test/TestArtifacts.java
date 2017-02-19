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

public class TestArtifacts extends TestBase {
	
	Reader reader;

	@Before
	public void beforeEachScenario() throws Exception {
	}
	
	@When("^a repo has the same name as the artifact$")
	public void a_repo_has_the_same_name_as_the_artifact() throws Throwable {

		String base = 
"namespace simple\n" +
" artifact ABC:2.3\n" +
"  tested with XYZ:3.3-3.4\n" +
" repo $RepoName type \"git\"\n" +
"  scheme \"https\" path \"github.com/myteam\" userid \"abc\" password \"def\"";

		String correct = base.replace("$RepoName", "my_repo");
		String incorrect = base.replace("$RepoName", "ABC");
		
		// First try it with a 'correct' version, to make sure our 'correct'
		// version does not have an error.
		Reader r = new StringReader(correct);
		Dabl dabl = new Dabl(false, true, r);
		createHelper(dabl.process());
		
		// Now try with the 'incorrect' version - this should generate an error
		// when it is processed in the next step.
		this.reader = new StringReader(incorrect);
	}
	
	@Then("^a SymbolEntryPresent error is generated when we process it$")
	public void a_SymbolEntryPresent_error_is_generated_when_we_process_it() throws Throwable {

		Dabl dabl = new Dabl(false, true, this.reader);
		try {
			createHelper(dabl.process());
		} catch (Exception ex) {
			Throwable t = ex.getCause();
			assertThat(t != null);
			assertThat(t instanceof SymbolEntryPresent);
			// Success - we expected this error
			return;
		}
		assertThat(false, "An exception was not thrown");
	}
	
	@When("^an artifact assumes compatibility with itself$")
	public void an_artifact_assumes_compatibility_with_itself() throws Throwable {

		this.reader = new StringReader(
"namespace simple\n" +
" artifact ABC:2.3\n" +
"  assume compatible with ABC:3.3-3.4");
	}
	
	@Then("^an error is generated when we process it$")
	public void an_error_is_generated_when_we_process_it() throws Throwable {

		Dabl dabl = new Dabl(false, true, this.reader);
		try {
			createHelper(dabl.process());
		} catch (Exception ex) {
			// Success - we expected an error
			return;
		}
		assertThat(false, "An exception was not thrown");
	}
	
	@When("^an artifact asserts tested with itself$")
	public void an_artifact_asserts_tested_with_itself() throws Throwable {
		
		this.reader = new StringReader(
"namespace simple\n" +
" artifact ABC:2.3\n" +
"  tested with ABC:3.3-3.4");
	}
	
	@When("^an artifact asserts compatibility with major version XYZ:\\*\\.3$")
	public void an_artifact_asserts_compatibility_with_major_version_XYZ() throws Throwable {
		
		this.reader = new StringReader(
"namespace simple\n" +
" artifact ABC:2.3\n" +
"  assume compatible with XYZ:*.3");
	}
	
	@Then("^a correct compatibility spec is generated$")
	public void a_correct_compatibility_spec_is_generated() throws Throwable {
		
		Node n = getHelper().getDeclaration("ABC");
		assertThat(n instanceof AOartifactDeclaration);
		AOartifactDeclaration artDecl = (AOartifactDeclaration)n;
		
		LinkedList<POcompatibilitySpec> compatibilities = artDecl.getOcompatibilitySpec();
		assertThat(compatibilities.size() == 1);
		POcompatibilitySpec s = compatibilities.get(0);
		assertThat(s instanceof AAssumeOcompatibilitySpec);
		AAssumeOcompatibilitySpec assumeSpec = (AAssumeOcompatibilitySpec)s;
		
		LinkedList<TId> pathIds = assumeSpec.getId();
		LinkedList<POrangeSpec> rangeSpecs = assumeSpec.getOrangeSpec();
		
		assertThat(pathIds.size() == 1);
		assertThat(pathIds.get(0).getText().equals("XYZ"));
		
		assertThat(rangeSpecs.size() == 2);
		POrangeSpec p = rangeSpecs.get(0);
		assertThat(p instanceof AAllOrangeSpec);
		
		p = rangeSpecs.get(1);
		assertThat(p instanceof AOneOrangeSpec);
		AOneOrangeSpec numSpec = (AOneOrangeSpec)p;
		TWholeNumber num = numSpec.getWholeNumber();
		assertThat(num.getText().equals("3"));
	}
	
	@When("^an artifact asserts compatibility with minor version XYZ:(\\d+)\\.\\*$")
	public void an_artifact_asserts_compatibility_with_minor_version_XYZ(int arg1) throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts compatibility with major and minor version XYZ:\\*\\.\\*$")
	public void an_artifact_asserts_compatibility_with_major_and_minor_version_XYZ() throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts compatibility with a range of versions, e\\.g\\., (\\d+)\\.(\\d+)-(\\d+)\\.(\\d+)$")
	public void an_artifact_asserts_compatibility_with_a_range_of_versions_e_g(int arg1, int arg2, int arg3, int arg4) throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts compatibility with a range of minor versions$")
	public void an_artifact_asserts_compatibility_with_a_range_of_minor_versions() throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts tested with a wildcard major version$")
	public void an_artifact_asserts_tested_with_a_wildcard_major_version() throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts tested with a wildcard minor version$")
	public void an_artifact_asserts_tested_with_a_wildcard_minor_version() throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts tested with a range of versions, e\\.g\\., (\\d+)\\.(\\d+)-(\\d+)\\.(\\d+)$")
	public void an_artifact_asserts_tested_with_a_range_of_versions_e_g(int arg1, int arg2, int arg3, int arg4) throws Throwable {
		
		throw new Exception();
	}
	
	@When("^an artifact asserts tested with a range of minor versions$")
	public void an_artifact_asserts_tested_with_a_range_of_minor_versions() throws Throwable {
		
		throw new Exception();
	}
}
