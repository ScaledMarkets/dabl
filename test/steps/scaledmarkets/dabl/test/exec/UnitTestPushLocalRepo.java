package scaledmarkets.dabl.test.exec;

import scaledmarkets.dabl.exec.LocalRepo;
import scaledmarkets.dabl.analyzer.*;
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
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.FileVisitResult;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.function.Predicate;

public class UnitTestPushLocalRepo extends TestBase {

	private boolean initialized = false;
	private static String TaskName = "t123";
	private static String NamespaceName = "simple";
	private static String OutputsName = "my_outputs";
	private LocalRepo repo;
	private File basedir = new File("UnitTestPushLocalRepo_scratch");
	private File givendir;
	
	private String base_dabl =
"namespace " + NamespaceName + " \n" +
"task " + TaskName + "\n" +
"  outputs " + OutputsName + " new local ";
	
	protected void initOnce() throws Exception {
		
		if (initialized) return;
		initialized = true;
		basedir.mkdir();
		//basedir.deleteOnExit();
	}
	
	
	@Before("@pushlocalrepo")
	public void beforeEachScenario() throws Exception {
		initOnce();
	}
	
	@After("@pushlocalrepo")
	public void afterEachScenario() throws Exception {
		//teardown(this.givendir);
	}
	

	// Scenario: 1: A single include pattern to push

	// Given 1:
	@Given("^there are two files in directory (\\d+), a\\.txt and b\\.txt$")
	public void there_are_two_files_in_a_directory_a_txt_and_b_txt(int dirNum) throws Throwable {
		
		setup(dirNum);
		
		// Create two files a.txt and b.txt in dir.
		File filea = new File(givendir, "a.txt");
		File fileb = new File(givendir, "b.txt");
		filea.createNewFile();
		fileb.createNewFile();
	}
	
	@When("^the include pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_b_txt() throws Throwable {

		String includePattern = "include \"b.txt\"";
		createDabl(includePattern);
		pushPatternsToRepo(includePattern, patternSets -> 
			patternSets.getIncludePatterns().size() == 1);
	}
	
	@Then("^only file b\\.txt is pushed$")
	public void only_file_b_txt_is_pushed() throws Throwable {
		
		// Get b.txt
		assertThat(this.repo.containsFile("b.txt"), new Runnable() {
			public void run() {
				System.out.println("b.txt not found");
				try {
					System.out.println("repo contains " + repo.countAllFiles() + " files:");
					for (String f : repo.listFiles()) {
						System.out.println("\t" + f);
					}
				} catch (Exception ex) { throw new RuntimeException(ex); }
			}
		});
		
		// Count all files - should only be one.
		long n = this.repo.countAllFiles();
		assertThat(n == 1, "Found " + n + " files");
	}
	
	
	// Scenario: 2: A single exclude pattern to push

	@When("^the exclude pattern specifies b\\.txt$")
	public void the_exclude_pattern_specifies_b_txt() throws Throwable {
		
		String excludePattern = "exclude \"b.txt\"";
		createDabl(excludePattern);
		pushPatternsToRepo(excludePattern, patternSets -> 
			patternSets.getExcludePatterns().size() == 1);
	}
	
	@Then("^no files are pushed$")
	public void no_files_are_pushed() throws Throwable {
		long n = this.repo.countAllFiles();
		assertThat(n == 0, "Found " + n + " files");
	}
	
	
	// Scenario: 3: One include pattern, and exclude one file, to push

	@When("^the include pattern specifies a\\.txt and b\\.txt, and the exclude pattern specifies b\\.txt$")
	public void the_include_pattern_specifies_a_txt_and_b_txt_and_the_exclude_pattern_specifies_b_txt() throws Throwable {
		
		String pattern = "include \"a.txt\", include \"b.txt\", exclude \"b.txt\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			(patternSets.getIncludePatterns().size() == 2) &&
			(patternSets.getExcludePatterns().size() == 1));
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

		(new File(givendir, "a.txt")).createNewFile();
		(new File(givendir, "b.txt")).createNewFile();
		(new File(givendir, "a.html")).createNewFile();
		(new File(givendir, "b.html")).createNewFile();
		(new File(givendir, "a.rtf")).createNewFile();
		(new File(givendir, "b.rtf")).createNewFile();
	}
	
	@When("^the include pattern specifies a\\.\\* and the exclude pattern specifies \\*\\.txt$")
	public void the_include_pattern_specifies_a_and_the_exclude_pattern_specifies_txt() throws Throwable {
		
		String pattern = "include \"a.*\", exclude \"*.txt\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			(patternSets.getIncludePatterns().size() == 1) &&
			(patternSets.getExcludePatterns().size() == 1));
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
		
		(new File(givendir, "a.txt")).createNewFile();
		(new File(givendir, "b.txt")).createNewFile();
		File d = new File(givendir, "d");
		d.mkdir();
		(new File(d, "a.txt")).createNewFile();
		(new File(d, "b.txt")).createNewFile();
		File dd = new File(d, "dd");
		dd.mkdir();
		(new File(dd, "a.txt")).createNewFile();
		(new File(dd, "b.txt")).createNewFile();
	}
	
	@When("^the include pattern specifies \\*\\*$")
	public void the_include_pattern_specifies_star_star() throws Throwable {
		
		String pattern = "include \"**\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			patternSets.getIncludePatterns().size() == 1);
	}
	
	@Then("^the files a\\.txt, d/a\\.txt, and d/dd/a\\.txt are pushed$")
	public void the_files_a_txt_d_a_txt_and_d_dd_a_txt_are_pushed() throws Throwable {
		assertThat(this.repo.containsFile("a.txt"), "a.txt not found");
		assertThat(this.repo.containsFile("d/a.txt"), "d/a.txt not found");
		assertThat(this.repo.containsFile("d/dd/a.txt"), "d/dd/a.txt not found");
		long n = this.repo.countAllFiles();
		assertThat(n == 6, "Found " + n + " files");
	}
	
	
	// Scenario: 6: One include pattern, and exclude a directory to push

	// Given 4:
	@Given("^directory (\\d+) with files a\\.txt, a\\.rtf, d/a\\.txt, d/a\\.rtf, e/a\\.txt, e/a\\.rtf, d/dd/a\\.txt, d/dd/a\\.rtf$")
	public void a_directory_with_filex_a_txt_a_rtf_d_a_txt_d_a_rtf_e_a_txt_e_a_rtf_d_dd_a_txt_d_dd_a_rtf(int dirNum) throws Throwable {
		
		setup(dirNum);

		(new File(givendir, "a.txt")).createNewFile();
		(new File(givendir, "a.rtf")).createNewFile();
		File d = new File(givendir, "d");
		d.mkdir();
		(new File(d, "a.txt")).createNewFile();
		(new File(d, "a.rtf")).createNewFile();
		File e = new File(givendir, "e");
		e.mkdir();
		(new File(e, "a.txt")).createNewFile();
		(new File(e, "a.rtf")).createNewFile();
		File dd = new File(d, "dd");
		dd.mkdir();
		(new File(dd, "a.txt")).createNewFile();
		(new File(dd, "a.rtf")).createNewFile();
	}
	
	@When("^the include pattern specifies \\*\\* and the exclude pattern specifies e$")
	public void the_include_pattern_specifies_star_star_and_the_exclude_pattern_specifies_e() throws Throwable {
		
		String pattern = "include \"**\", exclude \"e\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			(patternSets.getIncludePatterns().size() == 1) &&
			(patternSets.getExcludePatterns().size() == 1));
	}
	
	
	// Scenario: 7: Two include patterns, and exclude files that match a specified extension to push
	@When("^the include pattern specifies \\*\\* and the exclude pattern specifies \\*\\.txt$")
	public void the_include_pattern_specifies_star_star_and_the_exclude_pattern_specifies_txt() throws Throwable {
		
		String pattern = "include \"**\", exclude \"**.txt\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			(patternSets.getIncludePatterns().size() == 1) &&
			(patternSets.getExcludePatterns().size() == 1));
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
	
	
	// Scenario: 8: Two include patterns, and exclude files that match a specified extension to push
	@When("^the include patterns specify \\*\\* and \\* and the exclude pattern specifies \\*\\.txt$")
	public void the_include_patterns_specify_star_star_and_star_and_the_exclude_pattern_specifies_txt() throws Throwable {
		
		String pattern = "include \"**\", include \"*\", exclude \"**.txt\"";
		createDabl(pattern);
		pushPatternsToRepo(pattern, patternSets -> 
			(patternSets.getIncludePatterns().size() == 2) &&
			(patternSets.getExcludePatterns().size() == 1));
	}
	
	
	/* Shared methods */
	
	protected void setup(int dirNum) throws Exception {
		
		System.out.println("Test number " + dirNum + "----------------");
		this.givendir = new File(basedir, "given" + String.valueOf(dirNum));
		deleteDirectoryTree(this.givendir);
		this.givendir.mkdir(); // merely returns false if directory already exists.
	}
	
	protected void teardown(File givendir) throws Exception {
		deleteDirectoryTree(givendir);
	}
	
	protected void createDabl(String fileset) throws Exception {
		Reader reader = new StringReader(base_dabl + fileset);
		Dabl dabl = new Dabl(false, true, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null, "global scope is null");
	}

	protected void pushOutputsToRepo(ALocalOartifactSet localArtifactSet,
			LocalRepo repo, Predicate<PatternSets> predicate) throws Exception {
	
		LinkedList<POfilesetOperation> filesetOps = localArtifactSet.getOfilesetOperation();
		PatternSets patternSets = new PatternSets(repo);
		patternSets.assembleIncludesAndExcludes(getHelper(), filesetOps);
		
		System.out.println("patternSets=" + patternSets.toString());
		
		
		assertThat(predicate.test(patternSets), "pattern sets predicate failed");
		repo.putFiles(this.givendir, patternSets);
	}

	protected void pushPatternsToRepo(String pattern, Predicate<PatternSets> patternSetsPredicate) throws Exception {
		
		// Find the task's local artifact set.
		ALocalOartifactSet localArtifactSet = getHelper().findLocalArtifactSetForTask(
			TaskName, OutputsName);
		
		// Create a local repo.
		this.repo = LocalRepo.createRepo(NamespaceName, TaskName, OutputsName,
			localArtifactSet);
		
		assertThat(givendir.exists(), "Source dir " + givendir.toString() + " does not exist");
		System.out.println("Source dir " + givendir.toString() +
			" exists and contains " + givendir.list().length + " files:");
		printDirectoryTree(givendir);
		
		String workingDirPath = System.getProperty("user.dir");
		File workingDir = new File(workingDirPath);
		File namespaceDir = new File(workingDir, NamespaceName);
		File taskDir = new File(namespaceDir, TaskName);
		File repoDir = new File(taskDir, OutputsName);
		assertThat(repoDir.exists(), "Repo directory " + repoDir.toString() + " not found");
		System.out.println(repoDir.toString() + " exists, and contains " +
			repoDir.list().length + " files:");
		printDirectoryTree(repoDir);
		
		// Push the task's outputs to the local repo.
		pushOutputsToRepo(localArtifactSet, repo, patternSetsPredicate);

		assertThat(givendir.exists(), "Source dir " + givendir.toString() + " does not exist");
		System.out.println("Source dir " + givendir.toString() +
			" still exists and contains " + givendir.list().length + " files:");
		printDirectoryTree(givendir);
		
		assertThat(repoDir.exists(), "Repo directory " + repoDir.toString() + " not found");
		System.out.println(repoDir.toString() + " still exists, and contains " +
			repoDir.list().length + " files:");
		printDirectoryTree(repoDir);
	}
}
