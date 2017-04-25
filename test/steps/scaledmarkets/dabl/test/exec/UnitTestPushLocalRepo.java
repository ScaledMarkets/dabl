package scaledmarkets.dabl.test.exec;

import scaledmarkets.dabl.exec.LocalRepo;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;
import scaledmarkets.dabl.repos.DummyProvider;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en.And;

import java.io.Reader;
import java.io.StringReader;
import java.io.File;

public class UnitTestPushLocalRepo extends TestBase {

	private static String TaskName = "t123";
	private static String NamespaceName = "simple";
	private static String OutputsName = "my_outputs";
	private LocalRepo repo;
	private File basedir = new File("UnitTestPushLocalRepo_scratch");
	private File given1dir = new File(basedir, "given1");
	private File given2dir = new File(basedir, "given2");
	private File given3dir = new File(basedir, "given3");
	private File given4dir = new File(basedir, "given4");
	
	private String base_dabl =
"namespace " + NamespaceName + " \n" +
"repo my_repo new local\n" +
"task " + TaskName + "\n" +
"  outputs " + OutputsName + " of \"project1\" in my_repo "
	
	public UnitTestPushLocalRepo() {
		
		basedir.mkdir();
		basedir.deleteOnExit();
	}
	
	
	// Scenario: A single include pattern to push

	// Given 1:
	@Given("^there are two files in a directory, a\\.txt and b\\.txt$")
	public void there_are_two_files_in_a_directory_a_txt_and_b_txt() throws Throwable {
		
		this.given1dir.mkdir();
		this.given1dir.deleteOnExit();
		
		// Create two files a.txt and b.txt in dir.
		File filea = new File(given1dir, "a.txt");
		File fileb = new File(given1dir, "b.txt");
		filea.createNewFile();
		fileb.createNewFile();
		
		throw new Exception();
	}
	
	@When("^the include pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_b_txt() throws Throwable {

		String includePattern = "b.txt";
		createDabl(includePattern);
		
		this.repo = LocalRepo.createRepo(NamespaceName, TaskName,
			OutputsName, ....ALocalOartifactSet artifactSet);
		
		PatternSets patternSets = new PatternSets(repo);
		patternSets.assembleIncludesAndExcludes(getHelper(),
			....List<POfilesetOperation> filesetOps);
		
		repo.putFiles(this.given1dir, patternSets);

		throw new Exception();
	}
	
	@Then("^only file b\\.txt is pushed$")
	public void only_file_b_txt_is_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: A single exclude pattern to push

	@When("^the exclude pattern specifies b\\.txt$")
	public void the_exclude_pattern_specifies_b_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^no files are pushed$")
	public void no_files_are_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: One include pattern, and exclude one file, to push

	@When("^the include pattern specifies a\\.txt and b\\.txt, and the exclude pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_a_txt_and_b_txt_and_the_exclude_pattern_specifies_b_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^only file a\\.txt is pushed$")
	public void only_file_a_txt_is_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: One include pattern, and exclude files that match a specified extension to push

	// Given 2:
	@Given("^a directory contains files a\\.txt, b\\.txt, a\\.html, b\\.html, a\\.rtf, b\\.rtf$")
	public void a_directory_contains_files_a_txt_b_txt_a_html_b_html_a_rtf_b_rtf() throws Throwable {
		


		this.given2dir.mkdir();
		this.given2dir.deleteOnExit();

		throw new Exception();
	}
	
	@When("^the include pattern specifies a\\.\\* and the exclude pattern specifies \\*\\.txt$")
	public void the_include_pattern_specifies_a_and_the_exclude_pattern_specifies_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^only the files a\\.html and a\\.rtf are pushed$")
	public void only_the_files_a_html_and_a_rtf_are_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: Include a directory hierarchy

	// Given 3:
	@Given("^a directory with files a\\.txt, b\\.txt, d/a\\.txt, d/b\\.txt, d/dd/a\\.txt, d/dd/b\\.txt$")
	public void a_directory_with_files_a_txt_b_txt_d_a_txt_d_b_txt_d_dd_a_txt_d_dd_b_txt() throws Throwable {
		
		this.given3dir.mkdir();
		this.given3dir.deleteOnExit();

		throw new Exception();
	}
	
	@When("^the include pattern specifies \\*\\*/a\\.txt$")
	public void the_include_pattern_specifies_a_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^the files a\\.txt, d/a\\.txt, and d/dd/a\\.txt are pushed$")
	public void the_files_a_txt_d_a_txt_and_d_dd_a_txt_are_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: One include pattern, and exclude a directory to push

	// Given 4:
	@Given("^a directory with files a\\.txt, a\\.rtf, d/a\\.txt, d/a\\.rtf, e/a\\.txt, e/a\\.rtf, d/dd/a\\.txt, d/dd/a\\.rtf$")
	public void a_directory_with_filex_a_txt_a_rtf_d_a_txt_d_a_rtf_e_a_txt_e_a_rtf_d_dd_a_txt_d_dd_a_rtf() throws Throwable {
		
		this.given4dir.mkdir();
		this.given4dir.deleteOnExit();
		throw new Exception();
	}
	
	@When("^the include pattern specifies \\*\\*/\\*\\.txt and the exclude pattern specifies e$")
	public void the_include_pattern_specifies_txt_and_the_exclude_pattern_specifies_e() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	
	// Scenario: Two include patterns, and exclude files that match a specified extension to push
	@When("^the include pattern specifies \\* and \\*\\* and the exclude pattern specifies \\*\\*/\\*\\.txt$")
	public void the_include_pattern_specifies_and_and_the_exclude_pattern_specifies_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^the files a\\.rtf, d/a\\.rtf, d/dd/a\\.rtf, e/a\\.rtf are pushed$")
	public void the_files_a_rtf_d_a_rtf_d_dd_a_rtf_e_a_rtf_are_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	protected void createDabl(String fileset) {
		Reader reader = new StringReader(base_dabl + fileset);
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}
}
