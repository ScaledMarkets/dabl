package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.repos.DummyProvider;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

public class UnitTestOperateOnArtifacts extends TestBase {

	private String repoType;
	private static String TaskName = "t123";
	
	@Before("@operateonartifacts")
	public void beforeEachScenario() throws Exception {
	}

	@After("@operateonartifacts")
	public void afterEachScenario() throws Exception {
	}
	
	// Scenario: Basic
	
	@Given("^a dummy repo$")
	public void a_dummy_repo() throws Exception {
		this.repoType = DummyProvider.RepoType;
	}

	@When("^a single outputs specifies a single file$")
	public void a_single_outputs_specifies_a_single_file() throws Exception {

		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "\n" +
"  outputs of \"project2\" in my_repository include \"y\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getState().getGlobalScope() != null);
	}

	@Then("^the result specifies that file$")
	public void the_result_specifies_that_file() throws Throwable {
		
		DummyProvider dummy = process();
		List<String> files = dummy.getPushedFiles();
		assertThat(files.size() == 1, "Wrong number of results: " + files.size());
		String pattern = files.get(0);
		assertThat(pattern.equals("y"), "Mismatch in expected pattern: " + pattern);
	}

	
	// Scenario: Wildcard
	
	@When("^a single outputs specifies a wildcard pattern$")
	public void a_single_outputs_specifies_a_wildcard_pattern() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "\n" +
"  outputs of \"project2\" in my_repository \"abc.*\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getState().getGlobalScope() != null);
	}
	
	@Then("^the result specifies that pattern$")
	public void the_result_specifies_that_pattern() throws Exception {
		
		DummyProvider dummy = process();
		List<String> files = dummy.getPushedFiles();
		assertThat(files.size() == 1, "Wrong number of results");
		String pattern = files.get(0);
		assertThat(pattern.equals("abc.*"), "Mismatch in expected pattern: " + pattern);
	}
	
	
	// Scenario: Multiple outputs
	
	@When("^two outputs are specified$")
	public void two_outputs_are_specified() throws Exception {
		
		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "\n" +
"  outputs of \"project2\" in my_repository \"y\"\n" +
"  outputs of \"project2\" in my_repository \"/xyz/qrs\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getState().getGlobalScope() != null);
	}
	
	@Then("^the result specifies both of the patterns$")
	public void the_result_specifies_both_of_the_patterns() throws Exception {
		
		DummyProvider dummy = process();
		List<String> files = dummy.getPushedFiles();
		assertThat(files.size() == 2, "Wrong number of results");
		String pattern = files.get(0);
		assertThat(pattern.equals("y"), "Mismatch in expected pattern: " + pattern);
		pattern = files.get(1);
		assertThat(pattern.equals("/xyz/qrs"), "Mismatch in expected pattern: " + pattern);
	}
	
	
	/* Shared methods */
	
	protected DummyProvider process() throws Exception {
		
		// Identify the task.
		DependencyGraph graph = DependencyGraph.genDependencySet(getState());
		Set<Task> tasks = graph.getAllTasks();
		Task task = null;
		for (Task t : tasks) {
			if (t.getName().equals(TaskName)) {
				task = t;
				break;
			}
		}
		assertThat(task != null, "Could not identify task " + TaskName);
		
		// Prepare to invoke an artifact operator.
		Set<Artifact> outputs = task.getOutputs();
		Set<PosixFilePermission> permSet = PosixFilePermissions.fromString("rwx------");
		FileAttribute<Set<PosixFilePermission>> attr =
			PosixFilePermissions.asFileAttribute(permSet);
		File workspace = Files.createTempDirectory("dabl", attr).toFile();
		workspace.deleteOnExit();

		// Invoke an artifact operator.
		DummyProvider[] dummies = new DummyProvider[1];
		(new ArtifactOperator(this.helper) {
			protected void operation(PatternSets patternSets) throws Exception {
				
				Repo r = patternSets.getRepo();
				assertThat(r instanceof RemoteRepo, "r is a " + r.getClass().getName());
				RemoteRepo repo = (RemoteRepo)r;
				repo.getFiles(patternSets, workspace);
				RepoProvider provider = repo.getRepoProvider();
				assertThat(provider instanceof DummyProvider);
				dummies[0] = (DummyProvider)provider;
			}
		}).operateOnArtifacts(getHelper().getPrimaryNamespaceFullName(),
			task.getName(), outputs);
		
		assertThat(dummies[0] != null);
		return dummies[0];
	}
}
