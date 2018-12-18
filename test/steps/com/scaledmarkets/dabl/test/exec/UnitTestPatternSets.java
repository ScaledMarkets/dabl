package com.scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.scaledmarkets.dabl.client.*;
import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.exec.*;
import com.scaledmarkets.dabl.repos.DummyProvider;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.test.TestBase;
import com.scaledmarkets.dabl.exec.PatternSets;

import java.util.List;
import java.util.LinkedList;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;

public class UnitTestPatternSets extends TestBase {

	private boolean initialized = false;
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
"task " + TaskName + "\n" +
"  outputs " + OutputsName + " new local ";
	
	@Before("@patternsets")
	public void beforeEachScenario() throws Exception {
		initOnce();
	}

	@After("@patternsets")
	public void afterEachScenario() throws Exception {
		//teardown(this.givendir);
	}

	protected void initOnce() throws Exception {
		if (initialized) return;
		initialized = true;
		if (! basedir.mkdir()) System.out.println("Failed to make base dir");
		//basedir.deleteOnExit();
	}

	
	// Scenario: Basic
	
	@Given("^a working directory$")
	public void a_working_directory() throws Exception {
		setup(1);
		File filea = new File(givendir, "a.txt");
		File fileb = new File(givendir, "b.txt");
		filea.createNewFile();
		fileb.createNewFile();
	}
	
	@When("^I specify two include files and one exclude file$")
	public void i_specify_two_include_files_and_one_exclude_file() throws Exception {
		
		String pattern = "include \"a.txt\", include \"b.txt\", exclude \"b.txt\"";
		createDabl(pattern);

		// Find the task's local artifact set.
		POartifactSpec artifactSpec = getHelper().findArtifactSpecForTask(
			TaskName, OutputsName);
		AInlineOartifactSpec inlineArtifactSpec = (AInlineOartifactSpec)artifactSpec;
		POartifactSet artifactSet = inlineArtifactSpec.getOartifactSet();
		ALocalOartifactSet localArtifactSet = (ALocalOartifactSet)artifactSet;

		// Create a local repo.
		this.repo = LocalRepo.createRepo(NamespaceName, TaskName, OutputsName,
			localArtifactSet);

		List<POfilesetOperation> filesetOps = localArtifactSet.getOfilesetOperation();
		this.patternRoot = givendir;
		this.curDir = givendir;
		
		this.patternSets = new PatternSets(repo);
		this.patternSets.assembleIncludesAndExcludes(getHelper(), filesetOps);
		
		System.out.println("patternSets:");
		System.out.println(this.patternSets.toString());
		System.out.println();

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
		deleteDirectoryTree(givendir);
		if (! this.givendir.mkdir()) System.out.println("Failed to make given dir");
		assertThat(givendir.exists(), givendir + " does not exist");
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
}
