package scaledmarkets.dabl.test;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.helper.*;
import scaledmarkets.dabl.node.*;

import java.util.List;

/**
 * Utilities shared by the DABL Cucumber test suite.
 */
public class TestBase {

	protected Helper helper;
	
	public TestBase() {
	}
	
	protected Helper createHelper(CompilerState state) {
		this.helper = new Helper(state);
		return this.helper;
	}
	
	protected Helper getHelper() {
		return this.helper;
	}
	
	public void assertThat(boolean expr) throws Exception {
		this.helper.assertThat(expr);
	}
	
	public void assertThat(boolean expr, String msg) throws Exception {
		this.helper.assertThat(expr, msg);
	}
	
	public void assertThat(boolean expr, Runnable action) throws Exception {
		this.helper.assertThat(expr, action);
	}
	
	public void msg(String message) {
		System.out.println(message);
	}
	
	public void printAST(String title) {
		this.helper.printAST(title);
	}
}
