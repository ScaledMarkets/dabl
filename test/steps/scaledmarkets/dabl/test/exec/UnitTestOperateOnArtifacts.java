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


		(new ArtifactOperator(this.helper) {
			void operation(PatternSets patternSets) throws Exception;
				patternSets.getRepo().getFiles(patternSets, dir));
			}
		}).operateOnArtifacts();
}
