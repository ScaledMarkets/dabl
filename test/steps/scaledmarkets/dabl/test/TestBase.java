package scaledmarkets.dabl.test;

import scaledmarkets.dabl.main.*;
import java.util.List;

public class TestBase {

	protected CompilerState state;
	
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
	
	protected NameScopeEntry getNamespaceSymbolEntry(String namespaceName) {
		
		List<NameScope> scopeStack = this.state.scopeStack;
		SymbolEntry entry = this.state.scopeStack.get(0).getEntry(namespaceName);
		assertThat(entry != null);
		assertThat(entry instanceof NameScopeEntry);
		return (NameScopeEntry)entry;
	}
}
