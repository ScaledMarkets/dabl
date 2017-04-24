package scaledmarkets.dabl.test.exec;

import scaledmarkets.dabl.exec.LocalRepo;

public class UnitTestPushLocalRepo extends TestBase {

	private LocalRepo repo;
	
	@Given("^$")
	public void () throws Exception {
		
		this.repo = LocalRepo.createRepo(String namespaceName, String taskName,
			String outputName, ALocalOartifactSet artifactSet);
		
	}
	
	@When("^$")
	public void () throws Exception {
		
	}

	@Then("^$")
	public void () throws Exception {
		
	}
}
