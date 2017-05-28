package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import scaledmarkets.dabl.task.DablStandard;

import java.util.List;
import java.util.LinkedList;

/**
 * Perform symbol resolution and annotation:
 * 
 *	Create name scope for namespace and add it to the global scope.
 *	Concatenate imported namespaces.
 *	Create name scope for tasks and add it to the namespace.
 *	Add input and output names to their enclosing task's scope.
 *	Add function declarations to the namespace.
 *	Add files declarations to the namespace.
 *	Evaluate string literals and string expressions.
 */
public class LanguageAnalyzer extends DablBaseAdapter
{
	protected ImportHandler importHandler;
	protected NameScopeEntry enclosingScopeEntry;
	protected NameScope namespaceNamescope;
	
	public LanguageAnalyzer(CompilerState state, ImportHandler importHandler) {
		super(state);
		this.importHandler = importHandler;
		
		if (state.globalScope == null) {
			state.setGlobalScope(pushNameScope(new NameScope("Global", null, null)));
		}
	}
	
	public ImportHandler getImportHandler() { return importHandler; }
	
	public NameScopeEntry getEnclosingScopeEntry() { return enclosingScopeEntry; }
	
	public NameScope getNamespaceNamescope() { return namespaceNamescope; }
	
	
	/* Resolve references to declared names. */
	
    public void inAOidRef(AOidRef node)
    {
        defaultIn(node);
    }

    public void outAOidRef(AOidRef node)
    {
		TId id = node.getId();
		LinkedList<TId> path = new LinkedList<TId>();
		path.add(id);
    	outRefNode(node, path, new PublicVisibilityChecker(path));
    }
    
    public void inAOqualifiedNameRef(AOqualifiedNameRef node)
    {
    	defaultIn(node);
    }
    
    public void outAOqualifiedNameRef(AOqualifiedNameRef node)
    {
    	List<TId> path = node.getId();
    	outRefNode(node, path, new PublicVisibilityChecker(path));
    }
    
	
	/* Only onamespace and otask_declaration define name scopes. */
	
	public void inAOnamespace(AOnamespace node) {
		LinkedList<TId> path = node.getPath();
		String name = Utilities.createNameFromPath(path);
		
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = createNameScope(name, node);  // pushes name scope
											// and annotates the namespace Node.
		NameScopeEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		this.enclosingScopeEntry = entry;
		this.namespaceNamescope = newScope;
	}
	
	public void outAOnamespace(AOnamespace node) {
				
		....Check if there are unresolved symbols
		
		popNameScope();
	}
	
	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		
		importNamespace(Utilities.createNameFromPath(node.getId()));
	}
	
	
	/* Task declarations. */
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		
		TId id = node.getName();
		NameScope scope = createNameScope(id, node);
		
		POscope p = node.getOscope();
		if (p instanceof APublicOscope) scope.getSelfEntry().setDeclaredPublic();
		
		resolveForwardReferences(scope.getSelfEntry());
	}
	
	public void outAOtaskDeclaration(AOtaskDeclaration node) {
		popNameScope();
	}
	
	
	/* Add input and output names to their enclosing task's scope. */
	
	public void inANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
	{
		TId id = node.getId();
		
		DeclaredEntry entry;
		POartifactSet p = node.getOartifactSet();
		if (p instanceof ALocalOartifactSet) {
			entry = new LocalRepoEntry(id.getText(), getCurrentNameScope(), node);
		} else if (p instanceof ARemoteOartifactSet) {
			entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		} else throw new RuntimeException(
			"Unexpected Node kind: " + node.getClass().getName());
		
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		resolveForwardReferences(entry);
	}

	public void outANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
	{
		super.outANamedOnamedArtifactSet(node);
	}
	
	
	/* Artifact declaration. */
	
	public void inAOartifactDeclaration(AOartifactDeclaration node)
	{
		TId id = node.getId();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		resolveForwardReferences(entry);
	}
	
	public void outAOartifactDeclaration(AOartifactDeclaration node)
	{
		// Verify that artifact does not assert compatibility with itself.
		//....
		
		// Verify that artifact does not assert tested with itself.
		//....
	}
	
	
	/* Add function declarations to the namespace. */
	
	public void inAOfunctionDeclaration(AOfunctionDeclaration node)
	{
		TId id = node.getName();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		if (node.getOscope() instanceof APublicOscope) entry.setDeclaredPublic();
		resolveForwardReferences(entry);
	}

	public void outAOfunctionDeclaration(AOfunctionDeclaration node)
	{
		super.outAOfunctionDeclaration(node);
	}
	
	
	/* Add repo declarations to the namespace. */
	
	public void inAOrepoDeclaration(AOrepoDeclaration node)
	{
	 	TId id = node.getName();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		if (node.getOscope() instanceof APublicOscope) entry.setDeclaredPublic();
		resolveForwardReferences(entry);
	}
	
	public void outAOrepoDeclaration(AOrepoDeclaration node)
	{
		super.outAOrepoDeclaration(node);
	}
	
	
	/* Add files declarations to the namespace. */
	
	public void inAOfilesDeclaration(AOfilesDeclaration node)
	{
		TId id = node.getName();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		if (node.getOscope() instanceof APublicOscope) entry.setDeclaredPublic();
		resolveForwardReferences(entry);
	}

	public void outAOfilesDeclaration(AOfilesDeclaration node)
	{
		super.outAOfilesDeclaration(node);
	}
	
	
	/* Variables that are declared as return value targets in a function call. */
	
	public void inAFuncCallOprocStmt(AFuncCallOprocStmt node)
	{
		POtargetOpt p = node.getOtargetOpt();
		if (p instanceof ATargetOtargetOpt) {
			ATargetOtargetOpt targetOpt = (ATargetOtargetOpt)p;
			TId id = targetOpt.getId();
			
			// Add the target to the symbol table.
			DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
			try {
				addSymbolEntry(entry, id);
			} catch (SymbolEntryPresent ex) {
				throw new RuntimeException(ex);
			}
			resolveForwardReferences(entry);
		}
		
		// Add a semantic check: that function argument types and declared types match.
		/*
		If function declaration has been processed, perform the check now; otherwise,
		add it to the set of checks that should be performed when the function's
		declaration is processed.
		
		*/
		SymbolEntry entry = resolveSymbol(node.getOidRef());
		if (entry == null) {  // it is currently undeclared.
			Node ref = node.getOidRef();
			registerSemanticHandlerFor(ref,
				// Handler is called by the IdentHandler that is created in
				// DablBaseAdapter.outRefNode
				new IdentSemanticHandler(node.getOidRef()) {
					public void semanticAction(DeclaredEntry entry) {
						checkFuncCallTypes(node);
					}
				});
		} else {  // declaration found.
			checkFuncCallTypes(node);
		}
	}
		
	public void outAFuncCallOprocStmt(AFuncCallOprocStmt node)
	{
		super.outAFuncCallOprocStmt(node);
	}
	
	/**
	 * 
	 */
	void checkFuncCallTypes(AFuncCallOprocStmt funcCall) {
		
		POidRef idRef = funcCall.getOidRef();
		Annotation annot = getState().getOut(idRef);
		assertThat(annot != null, "Symbol " + idRef.toString() + " unidentified");
		assertThat(annot instanceof IdRefAnnotation,
			"Symbol " + idRef.toString() + " was not recognized as an Id reference");
		IdRefAnnotation idRefAnnot = (IdRefAnnotation)annot;
		SymbolEntry entry = idRefAnnot.getDefiningSymbolEntry();
		assertThat(entry instanceof DeclaredEntry,
			"Symbol " + idRef.toString() + " is not declared");
		DeclaredEntry declEntry = (DeclaredEntry)entry;
		Node defNode = declEntry.getDefiningNode();
		assertThat(defNode instanceof AOfunctionDeclaration,
			"Id " + idRef.getName() + " does not refer to a function declaration");
		
		AOfunctionDeclaration funcDecl = (AOfunctionDeclaration)defNode;
		List<ValueType> declaredArgTypes = new LinkedList<ValueType>();
		for /* each formal argument */ (POtypeSpec typeSpec : funcDecl.getOtypeSpec()) {
			declaredArgTypes.add(mapTypeSpecToValueType(typeSpec));
		}
		
		List<ValueType> argValueTypes = new LinkedList<ValueType>();
		for /* each actual argument */ (LinkedList<POexpr> arg : funcCall.getOexpr()) {
			
			ExprAnnotation annot = getExprAnnotation(arg);
			ValueType type = annot.getType();
			argValueTypes.add(type);
		}
		
		try { checkFunctionTypeConformance(declaredArgTypes, argValueTypes);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 
	 */
	public ValueType mapTypeSpecToValueType(POtypeSpec typeSpec) {
		if (typeSpec instanceof ANumericOtypeSpec) return ValueType.numeric;
		if (typeSpec instanceof AStringOtypeSpec) return ValueType.string;
		if (typeSpec instanceof ALogicalOtypeSpec) return ValueType.logical;
		if (typeSpec instanceof AArrayOtypeSpec) return ValueType.array;
		throw new RuntimeException("Unexpected typespec: " + typeSpec.getClass().getName());
	}
	
	/**
	 * 
	 */
	public checkFunctionTypeConformance(List<ValueType> declaredTypes,
		List<ValueType> actualTypes) throws Exception {
	
		assertThat(declaredTypes.size() == actualTypes.size(),
			"Mismatch in number of actual (" + actualTypes.size() + ") and declared (" +
			declaredTypes.size() + ") arguments");
		
		int i = 0;
		for (ValueType declaredType : declaredTypes) {
			ValueType actualType = actualTypes.get(i++);
			assertThat(actualType == declaredType,
				"Type " + actualType.toString() + " is not compatible with " +
				declaredType.toString());
		}
	}
	
	
	/* Evaluate string literals.
		ostring_literal =
			{string} string
		  | {string2} string2
		
		String token definitions:
		string = quote [ any_character - quote ]* quote;
		string2 = triplequote any_character* triplequote;
	 */

	public void inAStringOstringLiteral(AStringOstringLiteral node)
	{
		super.inAStringOstringLiteral(node);
	}

	public void outAStringOstringLiteral(AStringOstringLiteral node)
	{
		TString s = node.getString();
		String value = s.getText();
		
		// Remove double quotes that surround string.
		if (value.length() > 2) {
			value = value.substring(1, value.length()-1);
		}
		
		// Set attribute value.
		setExprAnnotation(node, value);
	}

	public void inAString2OstringLiteral(AString2OstringLiteral node)
	{
		super.inAString2OstringLiteral(node);
	}

	public void outAString2OstringLiteral(AString2OstringLiteral node)
	{
		TString2 s = node.getString2();
		String value = s.getText();
		
		// Remove double quotes that surround string.
		if (value.length() > 6) {
			value = value.substring(3, value.length()-5);
		}
		
		// Set attribute value.
		setExprAnnotation(node, value);
	}
	
	/* Evaluate string expressions.
	 * Production:
	 * ostring_literal =
	 *	{static_string_expr} [left]:ostring_literal [right]:ostring_literal
	 */
	 
	public void inAStaticStringExprOstringLiteral(AStaticStringExprOstringLiteral node)
	{
		super.inAStaticStringExprOstringLiteral(node);
	}

	public void outAStaticStringExprOstringLiteral(AStaticStringExprOstringLiteral node)
	{
		POstringLiteral left = node.getLeft();
		POstringLiteral right = node.getRight();
		
		ExprAnnotation leftAnnot = getExprAnnotation(left);
		ExprAnnotation rightAnnot = getExprAnnotation(right);
		
		Object leftValue;
		Object rightValue;
		
		leftValue = leftAnnot.getValue();
		rightValue = rightAnnot.getValue();
		
		if (leftValue == null) throw new RuntimeException("No left operand in concatenation");
		if (rightValue == null) throw new RuntimeException("No right operand in concatenation");
		
		if (! (leftValue instanceof String)) throw new RuntimeException("Left operand is not a string");
		if (! (rightValue instanceof String)) throw new RuntimeException("Right operand is not a string");
		
		String leftString = (String)leftValue;
		String rightString = (String)rightValue;
		
		String result = leftString + rightString;
		
		setExprAnnotation(node, result);
	}
}
