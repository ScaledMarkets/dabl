package scaledmarkets.dabl.test.exec;

import scaledmarkets.dabl.exec.LocalRepo;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

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
import java.io.IOException;
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.FileVisitResult
import java.nio.file.attribute.BasicFileAttributes
import java.util.List;
import java.util.LinkedList;
import java.util.Stream

public class UnitTestPushLocalRepo extends TestBase {

	private static String TaskName = "t123";
	private static String NamespaceName = "simple";
	private static String OutputsName = "my_outputs";
	private LocalRepo repo;
	private File basedir = new File("UnitTestPushLocalRepo_scratch");
	private File givendir;
	
	private String base_dabl =
"namespace " + NamespaceName + " \n" +
"repo my_repo new local\n" +
"task " + TaskName + "\n" +
"  outputs " + OutputsName + " of \"project1\" in my_repo "
	
	public UnitTestPushLocalRepo() {
		
		basedir.mkdir();
		basedir.deleteOnExit();
	}
	
	
	@Before
	public void beforeEachScenario() throws Exception {
	}
	
	@After
	public void afterEachScenario() throws Exception {
		teardown(this.givendir);
	}
	

	// Scenario: 1: A single include pattern to push

	// Given 1:
	@Given("^there are two files in directory (\\d+), a\\.txt and b\\.txt$")
	public void there_are_two_files_in_a_directory_a_txt_and_b_txt(String dirNum) throws Throwable {
		
		setup(dirNum);
		
		// Create two files a.txt and b.txt in dir.
		File filea = new File(givendir, "a.txt");
		File fileb = new File(givendir, "b.txt");
		filea.createNewFile();
		fileb.createNewFile();
	}
	
	@When("^the include pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_b_txt() throws Throwable {

		String includePattern = "b.txt";
		createDabl(includePattern);
		pushPatternsToRepo(includePattern, this.repo);
	}
	
	@Then("^only file b\\.txt is pushed$")
	public void only_file_b_txt_is_pushed() throws Throwable {
		
		// Get b.txt
		assertThat(this.repo.containsFile("b.txt"), "b.txt not found");
		
		// Count all files - should only be one.
		long n = this.repo.countAllFiles();
		assertThat(n == 1, "Found " + n + " files");
	}
	
	
	// Scenario: 2: A single exclude pattern to push

	@When("^the exclude pattern specifies b\\.txt$")
	public void the_exclude_pattern_specifies_b_txt() throws Throwable {
		
		String excludePattern = "exclude b.txt";
		createDabl(excludePattern);
		pushPatternsToRepo(excludePattern, this.repo);
	}
	
	@Then("^no files are pushed$")
	public void no_files_are_pushed() throws Throwable {
		long n = this.repo.countAllFiles();
		assertThat(n == 0, "Found " + n + " files");
		deleteDirContents(givendir);
	}
	
	
	// Scenario: 3: One include pattern, and exclude one file, to push

	@When("^the include pattern specifies a\\.txt and b\\.txt, and the exclude pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_a_txt_and_b_txt_and_the_exclude_pattern_specifies_b_txt() throws Throwable {
		
		String pattern = "include a.txt, b.txt exclude b.txt";
		createDabl(pattern);
		pushPatternsToRepo(pattern, this.repo);
	}
	
	@Then("^only file a\\.txt is pushed$")
	public void only_file_a_txt_is_pushed() throws Throwable {
		assertThat(this.repo.containsFile("a.txt"), "a.txt not found");
		long n = this.repo.countAllFiles();
		assertThat(n == 1, "Found " + n + " files");
	}
	
	
	// Scenario: 4: One include pattern, and exclude files that match a specified extension to push

	// Given 2:
	@Given("^directory (\\d+) contains files a\\.txt, b\\.txt, a\\.html, b\\.html, a\\.rtf, b\\.rtf$")
	public void a_directory_contains_files_a_txt_b_txt_a_html_b_html_a_rtf_b_rtf(int dirNum) throws Throwable {
		
		setup(dirNum);

		File filea = new File(givendir, "a.txt");
		File fileb = new File(givendir, "b.txt");
		File filec = new File(givendir, "a.html");
		File filed = new File(givendir, "b.html");
		File filee = new File(givendir, "a.rtf");
		File filef = new File(givendir, "b.rtf");
		filea.createNewFile();
		fileb.createNewFile();
		filec.createNewFile();
		filed.createNewFile();
		filee.createNewFile();
		filef.createNewFile();
		
		....continue here: complete the @Given methods.
	}
	
	@When("^the include pattern specifies a\\.\\* and the exclude pattern specifies \\*\\.txt$")
	public void the_include_pattern_specifies_a_and_the_exclude_pattern_specifies_txt() throws Throwable {
		
		String pattern = "a.*, exclude *.txt";
		createDabl(pattern);
		pushPatternsToRepo(pattern, this.repo);
	}
	
	@Then("^only the files a\\.html and a\\.rtf are pushed$")
	public void only_the_files_a_html_and_a_rtf_are_pushed() throws Throwable {
		assertThat(this.repo.containsFile("a.html"), "a.html not found");
		assertThat(this.repo.containsFile("a.rtf"), "a.rtf not found");
		long n = this.repo.countAllFiles();
		assertThat(n == 2, "Found " + n + " files");
	}
	
	
	// Scenario: 5: Include a directory hierarchy

	// Given 3:
	@Given("^directory (\\d+) with files a\\.txt, b\\.txt, d/a\\.txt, d/b\\.txt, d/dd/a\\.txt, d/dd/b\\.txt$")
	public void a_directory_with_files_a_txt_b_txt_d_a_txt_d_b_txt_d_dd_a_txt_d_dd_b_txt(int dirNum) throws Throwable {
		
		setup(dirNum);
		....
	}
	
	@When("^the include pattern specifies \\*\\*/a\\.txt$")
	public void the_include_pattern_specifies_a_txt() throws Throwable {
		
		String pattern = "**/a.txt";
		createDabl(pattern);
		pushPatternsToRepo(pattern, this.repo);
	}
	
	@Then("^the files a\\.txt, d/a\\.txt, and d/dd/a\\.txt are pushed$")
	public void the_files_a_txt_d_a_txt_and_d_dd_a_txt_are_pushed() throws Throwable {
		assertThat(this.repo.containsFile("a.txt"), "a.txt not found");
		assertThat(this.repo.containsFile("d/a.txt"), "d/a.txt not found");
		assertThat(this.repo.containsFile("d/dd/a.txt"), "d/dd/a.txt not found");
		long n = this.repo.countAllFiles();
		assertThat(n == 3, "Found " + n + " files");
	}
	
	
	// Scenario: 6: One include pattern, and exclude a directory to push

	// Given 4:
	@Given("^directory (\\d+) with files a\\.txt, a\\.rtf, d/a\\.txt, d/a\\.rtf, e/a\\.txt, e/a\\.rtf, d/dd/a\\.txt, d/dd/a\\.rtf$")
	public void a_directory_with_filex_a_txt_a_rtf_d_a_txt_d_a_rtf_e_a_txt_e_a_rtf_d_dd_a_txt_d_dd_a_rtf(int dirNum) throws Throwable {
		
		setup(dirNum);
		....
	}
	
	@When("^the include pattern specifies \\*\\*/\\*\\.txt and the exclude pattern specifies e$")
	public void the_include_pattern_specifies_txt_and_the_exclude_pattern_specifies_e() throws Throwable {
		
		String pattern = "**/*.txt exclude e";
		createDabl(pattern);
		pushPatternsToRepo(pattern, this.repo);
	}
	
	
	// Scenario: 7: Two include patterns, and exclude files that match a specified extension to push
	@When("^the include pattern specifies \\* and \\*\\* and the exclude pattern specifies \\*\\*/\\*\\.txt$")
	public void the_include_pattern_specifies_and_and_the_exclude_pattern_specifies_txt() throws Throwable {
		
		String pattern = "*, ** exclude **/*.txt";
		createDabl(pattern);
		pushPatternsToRepo(pattern, this.repo);
	}
	
	@Then("^the files a\\.rtf, d/a\\.rtf, d/dd/a\\.rtf, e/a\\.rtf are pushed$")
	public void the_files_a_rtf_d_a_rtf_d_dd_a_rtf_e_a_rtf_are_pushed() throws Throwable {
		assertThat(this.repo.containsFile("a.rtf"), "a.rtf not found");
		assertThat(this.repo.containsFile("d/a.rtf"), "d/a.rtf not found");
		assertThat(this.repo.containsFile("d/dd/a.rtf"), "d/dd/a.rtf not found");
		assertThat(this.repo.containsFile("e/a.rtf"), "e/a.rtf not found");
		long n = this.repo.countAllFiles();
		assertThat(n == 4, "Found " + n + " files");
	}
	
	protected void setup(int dirNum) throws Exception {
		
		this.givendir = new File(basedir, "given" + String.valueOf(dirNum));
		this.givendir.mkdir(); // merely returns false if directory already exists.
		deleteDirContents(givendir);
	}
	
	protected void teardown(File givendir) throws Exception {
		deleteDirContents(givendir);
		givendir.delete();
	}
	
	protected void createDabl(String fileset) {
		Reader reader = new StringReader(base_dabl + fileset);
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}

	protected ALocalOartifactSet findLocalArtifactSetForTask(String taskName,
		String outputsName) throws Exception {
		ANamedOnamedArtifactSet namedArtifactSet = getHelper().getNamedOutput(
			taskName, outputsName);
		POartifactSet p = namedArtifactSet.getOartifactSet();
		assertThat(p instanceof ALocalOartifactSet, "p is a " + p.getClass().getName());
		ALocalOartifactSet localArtifactSet = (ALocalOartifactSet)p;
		return localArtifactSet;
	}
	
	protected void pushOutputsToRepo(ALocalOartifactSet localArtifactSet,
		LocalRepo repo) throws Exception {
		LinkedList<POfilesetOperation> filesetOps = localArtifactSet.getOfilesetOperation();
		PatternSets patternSets = new PatternSets(repo);
		patternSets.assembleIncludesAndExcludes(getHelper(), filesetOps);
		repo.putFiles(this.givendir, patternSets);
	}

	protected void pushPatternsToRepo(String pattern) throws Exception {
		
		// Find the task's local artifact set.
		ALocalOartifactSet localArtifactSet = findLocalArtifactSetForTask(
			TaskName, OutputsName);
		
		// Create a local repo.
		this.repo = LocalRepo.createRepo(NamespaceName, TaskName, OutputsName,
			localArtifactSet);
		
		// Push the task's outputs to the local repo.
		pushOutputsToRepo(localArtifactSet, repo);
	}
	
	/**
	 * Delete all of the files and subdirectories in the specified directory.
	 */
	protected void deleteDirContents(File dir) throws Exception {
		if (! file.isDirectory()) throw new Exception("File " + dir.toString() + " is not a direcrtory");
		Stream<Path> paths = Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path> () {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (e == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw e; // directory iteration failed
				}
			}
		}
	}
}
