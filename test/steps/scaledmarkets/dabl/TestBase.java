package test;

import cucumber.api.Format;
import cucumber.api.Scenario;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.Before;
import cucumber.api.java.After;

import org.json.*;

public class TestBase {

	public Process process;

	private Scenario scenario;
	private String safeHarborHost;
	private int safeHarborPort;
	
	public TestBase() {
		try {
			safeHarborHost = Utils.getSafeHarborHost();
			safeHarborPort = Utils.getSafeHarborPort();
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
	
	@Before
	public void beforeEachScenario() throws Exception {
		//makeRequest("ClearAll");
	}
	
	@After
	public void afterEachScenario() throws Exception {
	}
	
	public void setScenario(Scenario s) { this.scenario = s; }
	
	public Scenario getScenario() { return scenario; }
	
	public String getSafeHarborHost() { return safeHarborHost; }
	
	public int getSafeHarborPort() { return safeHarborPort; }
}
