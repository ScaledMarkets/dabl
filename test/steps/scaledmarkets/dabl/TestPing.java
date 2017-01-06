package test;

import cucumber.api.Format;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.json.*;

import static test.Utils.*;

public class TestPing extends TestBase {
	
	String[] responses;  // stdout, stderr
	
	@When("^I send a Ping request$")
	public void i_send_a_Ping_request() throws Exception {
		responses = makeRequest("Ping");
	}
	
	@Then("^the ping HTTP response code should be (\\d+)$")
	public void the_ping_HTTP_response_code_should_be(int expected) throws Exception {
		
		try {
			JSONObject json = new JSONObject(responses[0]);
			Object obj = json.get("HTTPStatusCode");
			assertThat(obj instanceof Integer);
			assertThat(((Integer)obj).intValue() == expected);
		} catch (Exception ex) {
			throw new Exception("stdout=" + responses[0] + ", stderr=" + responses[1], ex);
		}
	}
}
