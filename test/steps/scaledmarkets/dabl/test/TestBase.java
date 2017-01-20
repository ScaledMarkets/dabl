package scaledmarkets.dabl.test;

public class TestBase {

	public TestBase() {
	}
	
	public void assertThat(boolean expr) throws Exception {
		assertThat(expr, null);
	}
	
	public void assertThat(boolean expr, String msg) throws Exception {
		if (msg == null) msg = "";
		if (msg != null) msg = "; " + msg;
		if (! expr) throw new Exception("Assertion violation: " + msg);
	}
}
