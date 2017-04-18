package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.repos.DummyProvider;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

public class UnitTestOperateOnArtifacts extends TestBase {

	private String repoType;
	private static String TaskName = "t123";
	
	// Scenario: Basic
	
	@Given("^a dummy repo$")
	public void a_dummy_repo() throws Exception {
		this.repoType = DummyProvider.RepoType;
	}

	@When("^a single outputs specifies a single file$")
	public void () throws Exception {

		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_repository type \"" + this.repoType + "\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task " + TaskName + "\n" +
"  outputs of \"project2\" in my_repository \"y\""
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getState().getGlobalScope() != null);
		
		
	}

	@Then("^the result specifies that file$")
	public void () throws Throwable {
		
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
		
		Set<Artifact> outputs = task.getOutputs();
		File workspace = Files.createTempDirectory("dabl", attr).toFile();
		workspace.deleteOnExit();

		List<String> files;
		(new ArtifactOperator(this.helper) {
			void operation(PatternSets patternSets) throws Exception {
				
				Repo repo = patternSets.getRepo();
				repo.getFiles(patternSets, workspace);
				RepoProvider provider = repo.getRepoProvider();
				assertThat(provider instanceof DummyProvider);
				DummyProvider dummy = (DummyProvider)provider;
				files = dummy.getPushedFiles();
			}
		}).operateOnArtifacts(outputs);
		
		assertThat(files.size() == 1, "Wrong number of results");
		String pattern = files.get(0);
		assertThat(pattern.equals("y"), "Mismatch in expected pattern: " + pattern);
	}

	
	// Scenario: Wildcard
	
	@When("^a single outputs specifies a wildcard pattern$")
	public void a_single_outputs_specifies_a_wildcard_pattern() throws Exception {
		
	}
	
	@Then("^the result specifies that pattern$")
	public void the_result_specifies_that_pattern() throws Exception {
		
	}
	
	
	// Scenario: Multiple outputs
	
	@When("^two outputs are specified$")
	public void two_outputs_are_specified() throws Exception {
		
	}
	
	@Then("^the result specifies both of the patterns$")
	public void the_result_specifies_both_of_the_patterns() throws Exception {
		
	}
}
