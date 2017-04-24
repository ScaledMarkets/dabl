package scaledmarkets.dabl.test.exec;

import scaledmarkets.dabl.exec.LocalRepo;

public class UnitTestPushLocalRepo extends TestBase {

	private LocalRepo repo;
	

	@Given("^there are two files in a directory, a\\.txt and b\\.txt$")
	public void there_are_two_files_in_a_directory_a_txt_and_b_txt() throws Throwable {
		
		this.repo = LocalRepo.createRepo(String namespaceName, String taskName,
			String outputName, ALocalOartifactSet artifactSet);
		
		throw new Exception();
	}
	
	@When("^the include pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_b_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@Then("^only file b\\.txt is pushed$")
	public void only_file_b_txt_is_pushed() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
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
	
	@Given("^a directory contains files a\\.txt, b\\.txt, a\\.html, b\\.html, a\\.rtf, b\\.rtf$")
	public void a_directory_contains_files_a_txt_b_txt_a_html_b_html_a_rtf_b_rtf() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
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
	
	@Given("^a directory with files a\\.txt, b\\.txt, d/a\\.txt, d/b\\.txt, d/dd/a\\.txt, d/dd/b\\.txt$")
	public void a_directory_with_files_a_txt_b_txt_d_a_txt_d_b_txt_d_dd_a_txt_d_dd_b_txt() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
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
	
	@Given("^a directory with files a\\.txt, a\\.rtf, d/a\\.txt, d/a\\.rtf, e/a\\.txt, e/a\\.rtf, d/dd/a\\.txt, d/dd/a\\.rtf$")
	public void a_directory_with_filex_a_txt_a_rtf_d_a_txt_d_a_rtf_e_a_txt_e_a_rtf_d_dd_a_txt_d_dd_a_rtf() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
	@When("^the include pattern specifies \\*\\*/\\*\\.txt and the exclude pattern specifies e$")
	public void the_include_pattern_specifies_txt_and_the_exclude_pattern_specifies_e() throws Throwable {
		// Write code here that turns the phrase above into concrete actions
		throw new Exception();
	}
	
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
}
