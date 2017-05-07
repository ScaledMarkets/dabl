package scaledmarkets.dabl.test.analyzer;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

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
			assertThat(t != null, "Throwable cause is null");
			assertThat(t instanceof SymbolEntryPresent, "Throwable is not a SymbolEntryPresent");
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
	
	@Then("^a compatibility spec is generated for version \\*\\.3$")
	public void a_compatibility_spec_is_generated_for_version_star_dot_3() throws Throwable {
		
		Node n = getHelper().getDeclaration("ABC");
		assertThat(n instanceof AOartifactDeclaration, "Node is not a AOartifactDeclaration");
		AOartifactDeclaration artDecl = (AOartifactDeclaration)n;
		
		LinkedList<POcompatibilitySpec> compatibilities = artDecl.getOcompatibilitySpec();
		assertThat(compatibilities.size() == 1, "Number of compatibilities is not 1");
		POcompatibilitySpec s = compatibilities.get(0);
		assertThat(s instanceof AAssumeOcompatibilitySpec, "compatibility is not a AAssumeOcompatibilitySpec");
		AAssumeOcompatibilitySpec assumeSpec = (AAssumeOcompatibilitySpec)s;
		
		LinkedList<TId> pathIds = assumeSpec.getId();
		LinkedList<POrangeSpec> rangeSpecs = assumeSpec.getOrangeSpec();
		
		assertThat(pathIds.size() == 1, "path ids size != 1");
		assertThat(pathIds.get(0).getText().equals("XYZ"), "path ids does not contain XYZ");
		
		assertThat(rangeSpecs.size() == 2, "range specs size != 2");
		POrangeSpec p = rangeSpecs.get(0);
		assertThat(p instanceof AAllOrangeSpec, "range spec is not a AAllOrangeSpec");
		
		p = rangeSpecs.get(1);
		assertThat(p instanceof AOneOrangeSpec, "p is not a AOneOrangeSpec");
		AOneOrangeSpec numSpec = (AOneOrangeSpec)p;
		TWholeNumber num = numSpec.getWholeNumber();
		assertThat(num.getText().equals("3"), "num is not 3");
	}
	
	@When("^an artifact asserts compatibility with minor version XYZ:(\\d+)\\.\\*$")
	public void an_artifact_asserts_compatibility_with_minor_version_XYZ(int arg1) throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a compatibility spec is generated for version 3\\.\\*$")
	public void a_compatibility_spec_is_generated_for_version_3_dot_star() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts compatibility with major and minor version XYZ:\\*\\.\\*$")
	public void an_artifact_asserts_compatibility_with_major_and_minor_version_XYZ() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a compatibility spec is generated for version \\*\\.\\*$")
	public void a_compatibility_spec_is_generated_for_version_star_dot_star() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts compatibility with a range of major versions, such as, 3\\.0-4\\.0$")
	public void an_artifact_asserts_compatibility_with_a_range_of_major_versions(int arg1, int arg2, int arg3, int arg4) throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a compatibility spec is generated for range 3\\.0 to 4\\.0$")
	public void a_compatibility_spec_is_generated_for_range_3_dot_0_to_4_dot_0() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts compatibility with a range of minor versions, such as, 3\\.3-3\\.4$")
	public void an_artifact_asserts_compatibility_with_a_range_of_minor_versions() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a compatibility spec is generated for range 3\\.3 to 3\\.4$")
	public void a_compatibility_spec_is_generated_for_range_3_dot_3_to_3_dot_4() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts tested with a wildcard major version, such as, *\\.3$")
	public void an_artifact_asserts_tested_with_a_wildcard_major_version() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a tested with spec is generated for version \\3\\.3$")
	public void a_tested_with_spec_is_generated_for_version_3_dot_3() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts tested with a wildcard minor version, such as, 3\\.\\*$")
	public void an_artifact_asserts_tested_with_a_wildcard_minor_version() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a tested with spec is generated for version 3\\.\\*$")
	public void a_tested_with_spec_is_generated_for_version_3_dot_star() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts tested with a range of major versions, such as, 3\\.3-4\\.0$")
	public void an_artifact_asserts_tested_with_a_range_of_major_versions() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a tested with spec is generated for versions 3\\.3-4\\.0$")
	public void a_tested_with_spec_is_generated_for_versions_3_dot_3_to_4_dot_0() throws Throwable {
		
		throw new Exception();
	}
		
	@When("^an artifact asserts tested with a range of minor versions, such as, 3\\.3-4\\.4$")
	public void an_artifact_asserts_tested_with_a_range_of_minor_versions() throws Throwable {
		
		throw new Exception();
	}
	
	@Then("^a tested with spec is generated for versions 3\\.3-4\\.4$")
	public void a_tested_with_spec_is_generated_for_versions_3_dot_3_to_4_dot_4() throws Throwable {
		
		throw new Exception();
	}
}
