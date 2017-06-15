package scaledmarkets.dabl.helper;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analyzer.*;

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
	
		Utilities.assertThat(start != null, "AST start symbol is null");
		POnamespace n = start.getPOnamespace();
		Utilities.assertThat(n instanceof AOnamespace, "namespace symbol is not a AOnamespace");
		AOnamespace namespace = (AOnamespace)n;
		return namespace;
	}
	
	/**
	 * Return the fully qualified name (i.e., all parts of the path) of the
	 * specified namespace.
	 */
	public String getPrimaryNamespaceFullName() throws Exception {
		Start start = state.getASTs().get(0);
		return getNamespaceFullName(getNamespace(start));
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
	 * Find the named artifact set node for the specified task and inputs or outputs clause.
	 */
	public ANamedOnamedArtifactSet getNamedOutput(String taskName, String inputsOrOutputsName)
	throws Exception {
		AOtaskDeclaration taskDecl = this.getTaskDeclaration(taskName);
		assertThat(taskDecl != null, "Could not find task " + taskName);
		NameScope taskNameScope = this.state.getNameScope(taskDecl);
		SymbolEntry entry = taskNameScope.getEntry(inputsOrOutputsName);
		assertThat(entry != null, "Could not find entry for " + inputsOrOutputsName);
		assertThat(entry instanceof DeclaredEntry, "entry is a " + entry.getClass().getName());
		DeclaredEntry dentry = (DeclaredEntry)entry;
		Node n = dentry.getDefiningNode();
		assertThat(n instanceof ANamedOnamedArtifactSet, "Node is a " + n.getClass().getName());
		ANamedOnamedArtifactSet namedArtifactSet = (ANamedOnamedArtifactSet)n;
		return namedArtifactSet;
	}
	
	/**
	 * Given a task name, and the name of a named local outputs clause, find the 
	 * LocalArtifactSet for the outputs clause.
	 */
	public ALocalOartifactSet findLocalArtifactSetForTask(String taskName,
			String outputsName) throws Exception {
		ANamedOnamedArtifactSet namedArtifactSet = getNamedOutput(
			taskName, outputsName);
		POartifactSet p = namedArtifactSet.getOartifactSet();
		assertThat(p instanceof ALocalOartifactSet, "p is a " + p.getClass().getName());
		ALocalOartifactSet localArtifactSet = (ALocalOartifactSet)p;
		return localArtifactSet;
	}
	
	/**
	 * Return the symbol table entry for the primary namespace. The entry is
	 * in the global symbol table.
	 */
	public NameScopeEntry getPrimaryNamespaceSymbolEntry() throws Exception {
		
		return state.getPrimaryNamespaceSymbolEntry();
	}
	
	/**
	 * Return the NameScopeEntry (in the global SymbolTable) for the specified namespace.
	 * The namespace can be the primary namespace, or an imported one.
	 */
	public NameScopeEntry getNamespaceSymbolEntry(String namespaceName) throws Exception {
		
		NameScope globalScope = this.state.getGlobalScope();
		SymbolEntry entry = globalScope.getEntry(namespaceName);
		Utilities.assertThat(entry != null, "No entry found for namespace name in global scope");
		Utilities.assertThat(entry instanceof NameScopeEntry,
			"namespace entry is not an instance of NameScopeEntry");
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
		Utilities.assertThat(e instanceof DeclaredEntry, "entry is a " + e.getClass().getName());
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
	 * Return the task declaration node that has the specified name.
	 */
	public AOtaskDeclaration getTaskDeclaration(String taskName) throws Exception {
		
		Node n = getDeclaration(taskName);
		Utilities.assertThat(n instanceof AOtaskDeclaration, "Declaration is not a AOtaskDeclaration");
		return (AOtaskDeclaration)n;
	}
	
	/**
	 * Return the specified Artifact declaration, from the primary namespace.
	 */
	public AOartifactDeclaration getArtifactDeclaration(String artifactName) throws Exception {
		
		Node n = getDeclaration(artifactName);
		Utilities.assertThat(n instanceof AOartifactDeclaration, "declaration is not a AOartifactDeclaration");
		return (AOartifactDeclaration)n;
	}
	
	/**
	 * Return the annotation representing the declaration of the symbol that is
	 * referenced by the specified Id ref.
	 */
	public DeclaredEntry getDeclaredEntryForIdRef(AOidRef idRef) {
		
		Annotation a = state.getOut(reposIdRef);
		if (a == null) throw new RuntimeException(
			"Unable to identify " + reposIdRef.getId());
		IdRefAnnotation reposIdRefAnnotation = null;
		if (a instanceof IdRefAnnotation) {
			reposIdRefAnnotation = (IdRefAnnotation)a;
		} else throw new RuntimeException("Unexpected annotation type: " + a.getClass().getName());
		SymbolEntry e = reposIdRefAnnotation.getDefiningSymbolEntry();
		if (! (e instanceof DeclaredEntry)) throw new RuntimeException(
			"Expected a DeclaredEntry, but found a " + e.getClass().getName());
		return (DeclaredEntry)e;
	}
	
	/**
	 * Return the declaration of the repository that is referenced by the specified Id ref.
	 */
	public AOrepoDeclaration getRepoDeclFromRepoRef(AOidRef reposIdRef) {
		
		DeclaredEntry de = getDeclaredEntryForIdRef(reposIdRef);
		Node n = de.getDefiningNode();
		if (! (n instanceof AOrepoDeclaration)) throw new RuntimeException(
			"Expected a AOrepoDeclaration, but found a " + de.getClass().getName());
		AOrepoDeclaration repoDecl = (AOrepoDeclaration)n;
		return repoDecl;
	}
	
	/**
	 * Return the named artifact declaration that is referenced by the specified
	 * id reference.
	 */
	public POnamedArtifactSet getNamedArtifactDeclFromArtfiactRef(AOidRef artIdRef) {
		
		DeclaredEntry entry = getDeclaredEntryForIdRef(artIdRef);
		Node n = entry.getDefiningNode();
		if (! (n instanceof POnamedArtifactSet)) throw new RuntimeException(
			"Expected a POnamedArtifactSet, but found a " + entry.getClass().getName());
		return (POnamedArtifactSet)n;
	}
	
	/**
	 * Return the value of the specified String literal symbol.
	 */
	public String getStringLiteralValue(POstringLiteral literal) throws Exception {
		
		Object obj = state.getOut(literal);
		Utilities.assertThat(obj instanceof ExprAnnotation, "Annotation is not a ExprAnnotation");
		ExprAnnotation annot = (ExprAnnotation)obj;
		Object value = annot.getValue();
		Utilities.assertThat(value instanceof String, "Expr value is not a string");
		String stringValue = (String)value;
		return stringValue;
	}
	
	/**
	 * Return either the string value, or null.
	 */
	public String getStringValueOpt(POstringValueOpt n) throws Exception {
		
		if (n instanceof ASpecifiedOstringValueOpt) {
			POstringLiteral lit = ((ASpecifiedOstringValueOpt)n).getOstringLiteral();
			return getStringLiteralValue(lit);
		} else if (n instanceof AUnspecifiedOstringValueOpt) {
			return null;
		} else throw new RuntimeException(
			"Unexpected Node type: " + n.getClass().getName());
	}
	
	/**
	 * Return the types of the arguments of the specified function declaration.
	 */
	public List<ValueType> getFunctionDeclTypes(AOfunctionDeclaration funcDecl) {
		
		List<ValueType> declaredArgTypes = new LinkedList<ValueType>();
		for /* each formal argument */ (POtypeSpec typeSpec : funcDecl.getOtypeSpec()) {
			declaredArgTypes.add(mapTypeSpecToValueType(typeSpec));
		}
		return declaredArgTypes;
	}
	
	/**
	 * Return the actual types of the argument expressions of the specified
	 * function call.
	 */
	public List<ValueType> getFunctionCallTypes(AFuncCallOprocStmt funcCall) {
		List<ValueType> argValueTypes = new LinkedList<ValueType>();
		for /* each actual argument */ (LinkedList<POexpr> arg : funcCall.getOexpr()) {
			
			ExprAnnotation annot = getExprAnnotation(arg);
			ValueType type = annot.getType();
			argValueTypes.add(type);
		}
		return argValueTypes;
	}
	
	/**
	 * If expr is false, print the message and throw an Exception.
	 */
	public static void assertThat(boolean expr, String msg) throws Exception {
		Utilities.assertThat(expr, msg);
	}
	
	/**
	 * If expr is false, perform the specified action and then throw an Exception.
	 */
	public static void assertThat(boolean expr, Runnable action) throws Exception {
		Utilities.assertThat(expr, action);
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
