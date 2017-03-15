package scaledmarkets.dabl.helper;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analysis.*;

import sablecc.PrettyPrint;

import java.util.List;
import java.util.LinkedList;

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
	 * Return the ASTs that were parsed by the compiler.
	 */
	public List<Start> getASTs() {
		return state.getASTs();
	}
	
	/**
	 * Return the primary namespace Node. The primary namespace is the namespace
	 * that is declared by the DABL that was processed. Other namespaces might
	 * have been imported by the DABL input, but those are not primary.
	 */
	public AOnamespace getPrimaryNamespace() throws Exception {
		Start start = state.getASTs().get(0);
		return getNamespace(start);
	}
	
	/**
	 * Return the namespace for the specified AST.
	 */
	public AOnamespace getNamespace(Start start) throws Exception {
	
		assertThat(start != null);
		POnamespace n = start.getPOnamespace();
		assertThat(n instanceof AOnamespace);
		AOnamespace namespace = (AOnamespace)n;
		return namespace;
	}
	
	/**
	 * Return the fully qualified name (i.e., all parts of the path) of the
	 * specified namespace.
	 */
	public String getNamespaceFullName(AOnamespace namespace) {
		LinkedList<TId> ids = namespace.getPath();
		String name = "";
		boolean firstTime = true;
		for (TId id : ids) {
			if (firstTime) { firstTime = false; } else {
				name = name + ".";
			}
			name = name + id.getText();
		}
		return name;
	}

	/**
	 * Return the top level Nodes that are owned by the primary namespace.
	 */
	public List<POnamespaceElt> getNamespaceElements() throws Exception {
		Start start = state.getASTs().get(0);
		return getNamespaceElements(start);
	}
	
	/**
	 * Return the top level Nodes that are owned by the namespace of the specified AST.
	 */
	public List<POnamespaceElt> getNamespaceElements(Start start) throws Exception {
		
		AOnamespace namespace = getNamespace(start);
		return namespace.getOnamespaceElt();
	}
	
	/**
	 * Return the imports of the primary namespace. Only namespaces that are directly
	 * imported are returned: any that are imported by those that are imported
	 * are not included.
	 */
	public List<AImportOnamespaceElt> getImportedNamespaces() throws Exception {
		Start start = state.getASTs().get(0);
		return getImportedNamespaces(start);
	}
	
	/**
	 * Return the imports of the namespace of the specified AST. Only namespaces
	 * that are directly imported are returned: any that are imported by those that
	 * are imported are not included.
	 */
	public List<AImportOnamespaceElt> getImportedNamespaces(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AImportOnamespaceElt> importElts = new LinkedList<AImportOnamespaceElt>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof AImportOnamespaceElt) {
				AImportOnamespaceElt importElt = (AImportOnamespaceElt)elt;
				importElts.add(importElt);
			}
		}
		return importElts;
	}
	
	/**
	 * Return the artifact declarations in the primary namespace.
	 */
	public List<AOartifactDeclaration> getArtifactDeclarations() throws Exception {
		Start start = state.getASTs().get(0);
		return getArtifactDeclarations(start);
	}
	
	/**
	 * Return the artifact declarations in the namespace of the specified AST.
	 */
	public List<AOartifactDeclaration> getArtifactDeclarations(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AOartifactDeclaration> decls = new LinkedList<AOartifactDeclaration>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof AArtifactOnamespaceElt) {
				AArtifactOnamespaceElt anelt = (AArtifactOnamespaceElt)elt;
				decls.add((AOartifactDeclaration)(anelt.getOartifactDeclaration()));
			}
		}
		return decls;
	}
	
	/**
	 * Return the repo declarations in the primary namespace.
	 */
	public List<AOrepoDeclaration> getRepoDeclarations() throws Exception {
		Start start = state.getASTs().get(0);
		return getRepoDeclarations(start);
	}
	
	/**
	 * Return the repo declarations in the namespace of the specified AST.
	 */
	public List<AOrepoDeclaration> getRepoDeclarations(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AOrepoDeclaration> decls = new LinkedList<AOrepoDeclaration>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof ARepoOnamespaceElt) {
				ARepoOnamespaceElt anelt = (ARepoOnamespaceElt)elt;
				decls.add((AOrepoDeclaration)(anelt.getOrepoDeclaration()));
			}
		}
		return decls;
	}
	
	/**
	 * Return the files declarations in the primary namespace.
	 */
	public List<AOfilesDeclaration> getFilesDeclarations() throws Exception {
		Start start = state.getASTs().get(0);
		return getFilesDeclarations(start);
	}
	
	/**
	 * Return the files declarations in the namespace of the specified AST.
	 */
	public List<AOfilesDeclaration> getFilesDeclarations(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AOfilesDeclaration> decls = new LinkedList<AOfilesDeclaration>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof AFilesOnamespaceElt) {
				AFilesOnamespaceElt anelt = (AFilesOnamespaceElt)elt;
				decls.add((AOfilesDeclaration)(anelt.getOfilesDeclaration()));
			}
		}
		return decls;
	}
	
	/**
	 * Return the function declarations in the primary namespace.
	 */
	public List<AOfunctionDeclaration> getFunctionDeclarations() throws Exception {
		Start start = state.getASTs().get(0);
		return getFunctionDeclarations(start);
	}
	
	/**
	 * Return the function declarations in the namespace of the specified AST.
	 */
	public List<AOfunctionDeclaration> getFunctionDeclarations(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AOfunctionDeclaration> decls = new LinkedList<AOfunctionDeclaration>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof AFunctionOnamespaceElt) {
				AFunctionOnamespaceElt anelt = (AFunctionOnamespaceElt)elt;
				decls.add((AOfunctionDeclaration)(anelt.getOfunctionDeclaration()));
			}
		}
		return decls;
	}
	
	/**
	 * Return the task declarations in the primary namespace.
	 */
	public List<AOtaskDeclaration> getTaskDeclarations() throws Exception {
		Start start = state.getASTs().get(0);
		return getTaskDeclarations(start);
	}
	
	/**
	 * Return the task declarations in the namespace of the specified AST.
	 */
	public List<AOtaskDeclaration> getTaskDeclarations(Start start) throws Exception {
		
		List<POnamespaceElt> elts = getNamespaceElements(start);
		List<AOtaskDeclaration> decls = new LinkedList<AOtaskDeclaration>();
		for (POnamespaceElt elt : elts) {
			if (elt instanceof ATaskOnamespaceElt) {
				ATaskOnamespaceElt anelt = (ATaskOnamespaceElt)elt;
				decls.add((AOtaskDeclaration)(anelt.getOtaskDeclaration()));
			}
		}
		return decls;
	}
	
	/**
	 * Return the symbol table entry for the primary namespace. The entry is
	 * in the global symbol table.
	 */
	public NameScopeEntry getPrimaryNamespaceSymbolEntry() throws Exception {
		AOnamespace namespace = getPrimaryNamespace();
		Annotation a = this.state.getIn(namespace);
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
		
		NameScope globalScope = this.state.getGlobalScope();
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
	
	public AOtaskDeclaration getTaskDeclaration(String taskName) throws Exception {
		
		Node n = getDeclaration(artifactName);
		assertThat(n instanceof AOtaskDeclaration);
		return (AOtaskDeclaration)n;
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
	
	/**
	 * If expr is false, throw an Exception.
	 */
	public void assertThat(boolean expr) throws Exception {
		if (! expr) throw new Exception("Assertion violation");
	}
	
	/**
	 * If expr is false, print the message and throw an Exception.
	 */
	public void assertThat(boolean expr, String msg) throws Exception {
		if (msg == null) msg = "";
		if (msg != null) msg = "; " + msg;
		if (! expr) throw new Exception("Assertion violation: " + msg);
	}
	
	/**
	 * If expr is false, perform the specified action and then throw an Exception.
	 */
	public void assertThat(boolean expr, Runnable action) throws Exception {
		if (! expr) {
			System.out.println("Assertion violation");
			action.run();
			throw new Exception("Assertion violation");
		}
	}
	
	/**
	 * Print title, followed by the primary AST to stdout. The primary AST is
	 * the first element of state.getASTs().
	 */
	public void printAST(String title) {
		Start start = state.getASTs().get(0);
		printAST(title, start);
	}
	
	/**
	 * Print title, followed by the specified AST to stdout.
	 */
	public void printAST(String title, Start start) {
		System.out.println(title);
		PrettyPrint.pp(start);
	}
}
