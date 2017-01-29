package scaledmarkets.dabl.test;

import scaledmarkets.dabl.main.*;
import scaledmarkets.dabl.node.*;

import java.util.List;

/**
 * Utilities shared by the DABL Cucumber test suite.
 */
public class TestBase {

	protected CompilerState state;
	
	public TestBase() {
	}
	
	public void assertThat(boolean expr) throws Exception {
		if (! expr) throw new Exception("Assertion violation");
	}
	
	public void assertThat(boolean expr, String msg) throws Exception {
		if (msg == null) msg = "";
		if (msg != null) msg = "; " + msg;
		if (! expr) throw new Exception("Assertion violation: " + msg);
	}
	
	public void assertThat(boolean expr, Runnable action) throws Exception {
		if (! expr) {
			System.out.println("Assertion violation");
			action.run();
			throw new Exception("Assertion violation");
		}
	}
	
	/**
	 * Return the NameScopeEntry (in the global SymbolTable) for the namespace.
	 */
	protected NameScopeEntry getNamespaceSymbolEntry(String namespaceName) throws Exception {
		
		List<NameScope> scopeStack = this.state.scopeStack;
		SymbolEntry entry = this.state.scopeStack.get(0).getEntry(namespaceName);
		assertThat(entry != null);
		assertThat(entry instanceof NameScopeEntry);
		return (NameScopeEntry)entry;
	}
	
	/**
	 * Return the DeclaredEntry for the specified top level symbol. The caller
	 * must provide the NameScopeEntry for the namespace.
	 */
	protected DeclaredEntry getDeclaredEntry(NameScopeEntry namespaceEntry, String name) throws Exception {
		
		NameScope scope = namespaceEntry.getOwnedScope();
		SymbolEntry e = scope.getEntry(name);
		assertThat(e != null);
		assertThat(e instanceof DeclaredEntry, "entry is a " + e.getClass().getName());
		return (DeclaredEntry)e;
	}
	
	/**
	 * Return the value of the specified String literal symbol.
	 */
	protected String getStringLiteralValue(POstringLiteral literal) throws Exception {
		
		Object obj = state.getOut(literal);
		assertThat(obj instanceof ExprAnnotation);
		ExprAnnotation annot = (ExprAnnotation)obj;
		Object value = annot.getValue();
		assertThat(value instanceof String);
		String stringValue = (String)value;
		return stringValue;
	}
}
