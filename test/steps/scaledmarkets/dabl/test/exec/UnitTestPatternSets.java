package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.repos.DummyProvider;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;
import scaledmarkets.dabl.exec.PatternSets;

import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;

public class UnitTestPatternSets extends TestBase {

	private LocalRepo repo;
	private File basedir = new File("UnitTestPatternSets_scratch");
	private File givendir;

	private PatternSets patternSets;
	private File patternRoot;
	private File curDir;
	
	List<String> results = new LinkedList<String>();
	
	private static String TaskName = "t123";
	private static String NamespaceName = "simple";
	private static String OutputsName = "my_outputs";

	private String base_dabl =
"namespace " + NamespaceName + " \n" +
"repo my_repo new local\n" +
"task " + TaskName + "\n" +
"  outputs " + OutputsName + " of \"project1\" in my_repo ";
	
	public UnitTestPatternSets() {
		
		basedir.mkdir();
		basedir.deleteOnExit();
	}

	
	// Scenario: Basic
	
	@Given("^a working directory$")
	public void a_working_directory() throws Exception {
		
		setup(1);
	}
	
	@When("^I specify two include files and one exclude file$")
	public void i_specify_two_include_files_and_one_exclude_file() throws Exception {
		
		String pattern = "a.txt, b.txt exclude b.txt";
		createDabl(pattern);

		// Find the task's local artifact set.
		ALocalOartifactSet localArtifactSet = getHelper().findLocalArtifactSetForTask(
			TaskName, OutputsName);

		// Create a local repo.
		this.repo = LocalRepo.createRepo(NamespaceName, TaskName, OutputsName,
			localArtifactSet);

		List<POfilesetOperation> filesetOps = localArtifactSet.getOfilesetOperation();
		this.patternRoot = givendir;
		this.curDir = givendir;
		
		this.patternSets = new PatternSets(repo);
		this.patternSets.assembleIncludesAndExcludes(getHelper(), filesetOps);

		this.patternSets.operateOnFiles(this.patternRoot, this.curDir, new PatternSets.FileOperator() {
			public void op(File root, String pathRelativeToRoot) throws Exception {
				UnitTestPatternSets.this.results.add(pathRelativeToRoot);
			}
		});
	}
	
	@Then("^one file path is processed$")
	public void one_file_path_is_processed() throws Exception {
		assertThat(results.size() == 1, "There are " + results.size() + " results");
		teardown(this.givendir);
	}
	
	
	/* Shared methods */
	
	protected void setup(int dirNum) throws Exception {
		
		this.givendir = new File(basedir, "given" + String.valueOf(dirNum));
		//this.givendir.mkdir(); // merely returns false if directory already exists.
		//deleteDirContents(givendir);
	}
	
	protected void teardown(File givendir) throws Exception {
		//deleteDirContents(givendir);
		//givendir.delete(); // merely returns false if directory does not exist.
	}

	protected void createDabl(String fileset) throws Exception {
		Reader reader = new StringReader(base_dabl + fileset);
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}
}
