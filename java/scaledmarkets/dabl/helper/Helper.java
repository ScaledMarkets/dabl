package scaledmarkets.dabl.helper;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.main.*;

import sablecc.PrettyPrint;

/**
 * Convenience methods for traversing the final AST and Annotations that are produced
 * by the DABL compiler.
 */
public class Helper {
	
	private CompilerState state;
	
	public Helper(CompilerState state) {
		this.state = state;
	}
	
	public CompilerState getState() {
		return this.state;
	}
	
	/**
	 * Return the NameScopeEntry (in the global SymbolTable) for the namespace.
	 */
	public NameScopeEntry getNamespaceSymbolEntry(String namespaceName) throws Exception {
		
		NameScope globalScope = this.state.globalScope;
		SymbolEntry entry = globalScope.getEntry(namespaceName);
		assertThat(entry != null);
		assertThat(entry instanceof NameScopeEntry);
		return (NameScopeEntry)entry;
	}
	
	/**
	 * Return the DeclaredEntry for the specified top level symbol. The caller
	 * must provide the NameScopeEntry for the namespace.
	 */
	public DeclaredEntry getDeclaredEntry(NameScopeEntry namespaceEntry, String name) throws Exception {
		
		NameScope scope = namespaceEntry.getOwnedScope();
		SymbolEntry e = scope.getEntry(name);
		assertThat(e != null);
		assertThat(e instanceof DeclaredEntry, "entry is a " + e.getClass().getName());
		return (DeclaredEntry)e;
	}
	
	/**
	 * Return the value of the specified String literal symbol.
	 *
	public String getStringLiteralValue(POstringLiteral literal) throws Exception {
		
		Object obj = state.getOut(literal);
		assertThat(obj instanceof ExprAnnotation);
		ExprAnnotation annot = (ExprAnnotation)obj;
		Object value = annot.getValue();
		assertThat(value instanceof String);
		String stringValue = (String)value;
		return stringValue;
	}*/
	
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
}
