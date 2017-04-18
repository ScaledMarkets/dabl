package scaledmarkets.dabl.test.exec;

import cucumber.api.Format;
import cucumber.api.java.Before;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.test.TestBase;

public class UnitTestOperateOnArtifacts extends TestBase {
	
	@When("^$")
	public void () throws Exception {

		Reader reader = new StringReader(
"namespace simple \n" +
"repo my_git type \"dummy\" scheme \"https\" path \"github.com/myteam\"\n" +
"  userid \"GitUserId\" password \"GitPassword\" \n" +
"task t123\n" +
"  inputs of \"project1\" in my_git \"x\"\n" +
"  outputs of \"project2\" in my_git \"y\"\n" +
"  abc = ff true"
			);
		
		Dabl dabl = new Dabl(false, true, reader);
		createHelper(dabl.process());
		assertThat(getHelper().getState().getGlobalScope() != null);
	}

	@Then("^$")
	public void () throws Throwable {
		Set<Artifact> inputs = ....task.getInputs();
		File workspace = Files.createTempDirectory("dabl", attr).toFile();

		(new ArtifactOperator(this.helper) {
			void operation(PatternSets patternSets) throws Exception {
				
				patternSets.getRepo().getFiles(patternSets, workspace);
			}
		}).operateOnArtifacts(inputs);
	}
}
