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
	
	protected DeclaredEntry getDeclaredEntry(NameScopeEntry nameScopeEntry, String name) {
		
		NameScope scope = nameScopeEntry.getOwnedScope();
		SymbolEntry e = scope.getEntry(name);
		assertThat(e != null);
		assertThat(e instanceof DeclaredEntry);
		return (DeclaredEntry)e;
	}
	
	protected String getStringLiteralValue(POstringLiteral literal) {
		
		Object obj = state.getOut(literal);
		assertThat(obj instanceof ExprAnnotation);
		ExprAnnotation annot = (ExprAnnotation)obj;
		Object value = annot.getValue();
		assertThat(value instanceof String);
		String stringValue = (String)value;
		return stringValue;
	}
}
