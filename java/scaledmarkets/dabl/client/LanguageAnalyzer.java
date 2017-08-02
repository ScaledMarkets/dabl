package scaledmarkets.dabl.client;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.util.Utilities;

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
public class LanguageAnalyzer extends LanguageCoreAnalyzer
{
	LanguageAnalyzer(CompilerState state, ImportHandler importHandler) {
		super(state, importHandler);
	}
	
	
	/* Unsupported features. */


	public void inAOtypographicDeclaration(AOfilesDeclaration node) {
		System.out.println("Not supported yet: " + node.getClass().getName());
	}

	public void inAOtranslationDeclaration(AOfilesDeclaration node) {
		System.out.println("Not supported yet: " + node.getClass().getName());
	}

	
	/* Resolve references to declared names. */
	
    public void inAOidRef(AOidRef node)
    {
        defaultIn(node);
    }

    public void outAOidRef(AOidRef node)
    {
		LinkedList<TId> path = node.getId();
    	outRefNode(node, path, new PublicVisibilityChecker(path));
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
	}
		
	public void outAFuncCallOprocStmt(AFuncCallOprocStmt node)
	{
		// Add a semantic check: that function argument types and declared types match.
		// If function declaration has been processed, perform the check now; otherwise,
		// add it to the set of checks that should be performed when the function's
		// declaration is processed.
		List<TId> path = ((AOidRef)(node.getOidRef())).getId();
		SymbolEntry entry = resolveSymbol(path);
		if (entry == null) {  // it is currently undeclared.
			AOidRef ref = (AOidRef)(node.getOidRef());
			registerSemanticHandlerFor(ref,
				// Handler is called by the IdentHandler that is created in
				// DablBaseAdapter.outRefNode
				new IdentSemanticHandler(ref) {
					public void semanticAction(DeclaredEntry entry) {
						checkFuncCallTypes(node);
					}
				});
		} else {  // declaration found.
			checkFuncCallTypes(node);  // assumes that the actual arg expressions
				// have already been analyzed.
		}
	}
	
	/**
	 * Verify that the actual arguments of the specified function call match the
	 * declared argument types for the function. It is assumed that the function
	 * declaration has been analyzed.
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
			"Id " + idRef.toString() + " does not refer to a function declaration");
		
		AOfunctionDeclaration funcDecl = (AOfunctionDeclaration)defNode;
		
		List<ValueType> declaredArgTypes = getFunctionDeclTypes(funcDecl);
		List<ValueType> argValueTypes = getFunctionCallTypes(getState(), funcCall);
		
		try { ValueType.checkTypeListAssignabilityTo(argValueTypes, declaredArgTypes);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
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
		setExprAnnotation(node, value, ValueType.string);
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
		setExprAnnotation(node, value, ValueType.string);
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
		
		setExprAnnotation(node, result, ValueType.string);
	}
}
