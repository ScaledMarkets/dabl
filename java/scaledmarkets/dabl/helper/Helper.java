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
	 * Return the root namespace Node.
	 */
	public AOnamespace getPrimaryNamespace() throws Exception {
		Start start = state.asts.get(0);
		assertThat(start != null);
		POnamespace n = start.getPOnamespace();
		assertThat(n instanceof AOnamespace);
		AOnamespace namespace = (AOnamespace)n;
		return namespace;
	}
	
	/**
	 * Return the symbol table entry for the primary namespace. The entry is
	 * in the global symbol table.
	 */
	public NameScopeEntry getPrimaryNamespaceSymbolEntry() throws Exception {
		AOnamespace namespace = getPrimaryNamespace();
		Annotation a = this.state.in.get(namespace);
		assertThat(a != null);
		assertThat(a instanceof NameScope);
		NameScope nameScope = (NameScope)a;
		NameScopeEntry entry = nameScope.getSelfEntry();
		assertThat(entry != null);
		return entry;
	}
	
	/**
	 * Return the NameScopeEntry (in the global SymbolTable) for the specified namespace.
	 * The namespace can be the primary namespace, or an imported one.
	 */
	public NameScopeEntry getNamespaceSymbolEntry(String namespaceName) throws Exception {
		
		NameScope globalScope = this.state.globalScope;
		SymbolEntry entry = globalScope.getEntry(namespaceName);
		assertThat(entry != null);
		assertThat(entry instanceof NameScopeEntry);
		return (NameScopeEntry)entry;
	}
	
	/**
	 * Return the DeclaredEntry for the specified top level symbol of a
	 * namespace. The caller must provide the NameScopeEntry for the namespace.
	 */
	public DeclaredEntry getDeclaredEntry(NameScopeEntry namespaceEntry, String name) throws Exception {
		
		NameScope scope = namespaceEntry.getOwnedScope();
		SymbolEntry e = scope.getEntry(name);
		if (e == null) return null;
		assertThat(e instanceof DeclaredEntry, "entry is a " + e.getClass().getName());
		return (DeclaredEntry)e;
	}
	
	/**
	 * Return the DeclaredEntry for the specified top level symbol of the primary
	 * namespace.
	 */
	public DeclaredEntry getDeclaredEntry(String name) throws Exception {
		
		NameScopeEntry namespaceEntry = getPrimaryNamespaceSymbolEntry();
		return getDeclaredEntry(namespaceEntry, name);
	}
	
	/**
	 * Return the Node corresponding to the declaration that is identified by
	 * the specified name.
	 */
	public Node getDeclaration(String name) throws Exception {
		
		DeclaredEntry entry = getDeclaredEntry(name);
		if (entry == null) return null;
		return entry.getDefiningNode();
	}
	
	/**
	 * Return the specified Artifact declaration, from the primary namespace.
	 */
	public AOartifactDeclaration getArtifactDeclaration(String artifactName) throws Exception {
		
		Node n = getDeclaration(artifactName);
		assertThat(n instanceof AOartifactDeclaration);
		return (AOartifactDeclaration)n;
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
	
	public void printAST(String title) {
		System.out.println(title);
		PrettyPrint.pp(state.asts.get(0));
	}
}
