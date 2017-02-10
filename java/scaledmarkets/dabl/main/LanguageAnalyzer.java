package scaledmarkets.dabl.main;

//import scaledmarkets.dabl.analysis.*;
//import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
//import scaledmarkets.dabl.parser.*;

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
	protected NameScope namespaceNamescope;
	
	public LanguageAnalyzer(CompilerState state, ImportHandler importHandler) {
		super(state);
		this.importHandler = importHandler;
		
		if (state.globalScope == null) {
			state.setGlobalScope(pushNameScope(new NameScope("Global", null, null)));
		}
	}
	
	public ImportHandler getImportHandler() { return importHandler; }
	
	public NameScope getNamespaceNamescope() { return namespaceNamescope; }
	
	
	/* Resolve references to declared names. */
	
    public void inAOidRef(AOidRef node)
    {
        defaultIn(node);
    }

    public void outAOidRef(AOidRef node)
    {
		// Find the declaration of the id. If it exists, annotate it with
		// 
		TId id = node.getId();
		LinkedList<TId> path = new LinkedList<TId>();
		path.add(id);
		SymbolEntry entry = resolveSymbol(path);
		if (entry == null) {
			// Might be a reference to something that is declared later.
			// 
			// Identify all the enclosing scopes, and attach a handler to each one.
			// The handler should be invoked whenever a new symbol is entered
			// into a scope. The handler should annotate the entry,
			// and then remove itself from all of the scopes to which it is attached.
			new IdentHandler(this, path, getCurrentNameScope()) {
				public void resolveRetroactively(DeclaredEntry entry)
				{
					setIdRefAnnotation(node, entry);
				}
				// Note: the base class, IdentHandler, contains a method
				// checkForPathResolution, which calls resolveRetroactively, 
				// followed by removeFromAllScopes().
			};
			
		} else {
			// Attribute the Id reference with the DeclaredEntry that defines the Id.
			if (! (entry instanceof DeclaredEntry)) throw new RuntimeException(
				"Unexpected: entry is a " + entry.getClass().getName());
			DeclaredEntry declent = (DeclaredEntry)entry;
			setIdRefAnnotation(node, declent);
		}
    }
	
	
	/* Only onamespace and otask_declaration define name scopes. */
	
	public void inAOnamespace(AOnamespace node) {
		LinkedList<TId> path = node.getPath();
		String name = createNameFromPath(path);
		
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = createNameScope(name, node);  // pushes name scope
		SymbolEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		this.namespaceNamescope = newScope;
	}
	
	public void outAOnamespace(AOnamespace node) {
		popNameScope();
	}
	
	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		
		System.out.println(">>>>>>>>Entered inAImportOnamespaceElt...");  // debug
		
		
		
		String name = createNameFromPath(node.getId());

		
		System.out.println(">>>>>>>>created name: " + name + "...");  // debug
		
		
		// Replace NameScope stack with a fresh one.
		CompilerState state = getState();
		List<NameScope> originalScopeStack = state.scopeStack;
		state.scopeStack = new LinkedList<NameScope>();
		state.pushScope(state.globalScope);
		
		
		NameScope importedScope = getImportHandler().importNamespace(name, getState());

		
		System.out.println(">>>>>>>>Called importNamespace...");  // debug
		
		
		// Restore NameScope stack.
		state.scopeStack = originalScopeStack;
		
		
		
		getState().globalScope.getSymbolTable().appendTable(importedScope.getSymbolTable());

	
	
		System.out.println(">>>>>>>>Leaving inAImportOnamespaceElt.");  // debug
	
	}
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		
		TId id = node.getName();
		createNameScope(id, node);
	}
	
	public void outAOtaskDeclaration(AOtaskDeclaration node) {
		popNameScope();
	}
	
	
	/* Add input and output names to their enclosing task's scope. */
	
	public void inANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
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

	public void outANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
	{
		super.outANamedOnamedArtifactSet(node);
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
		resolveForwardReferences(entry);
	}

	public void outAOfunctionDeclaration(AOfunctionDeclaration node)
	{
		super.outAOfunctionDeclaration(node);
	}
	
	
	/* Add repo declarations to the namespace. */
	
	public void inAOrepoDecl(AOrepoDecl node)
	{
	 	TId id = node.getName();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		resolveForwardReferences(entry);
	}
	
	public void outAOrepoDecl(AOrepoDecl node)
	{
		super.outAOrepoDecl(node);
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
		super.outAFuncCallOprocStmt(node);
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
		TString s = node.getString();
		String value = s.getText();
		
		// Remove double quotes that surround string.
		if (value.length() > 2) {
			value = value.substring(1, value.length()-1);
		}
		
		// Set attribute value.
		//setExprAnnotation(node, value);
	}

	public void outAStringOstringLiteral(AStringOstringLiteral node)
	{
		super.outAStringOstringLiteral(node);
	}

	public void inAString2OstringLiteral(AString2OstringLiteral node)
	{
		TString2 s = node.getString2();
		String value = s.getText();
		
		// Remove double quotes that surround string.
		if (value.length() > 6) {
			value = value.substring(3, value.length()-5);
		}
		
		// Set attribute value.
		//setExprAnnotation(node, value);
	}

	public void outAString2OstringLiteral(AString2OstringLiteral node)
	{
		super.outAString2OstringLiteral(node);
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
		/*
		POstringLiteral left = node.getLeft();
		POstringLiteral right = node.getRight();
		
		ExprAnnotation leftAnnot = getExprAnnotation(left);
		ExprAnnotation rightAnnot = getExprAnnotation(right);
		
		Object leftValue;
		Object rightValue;
		
		try {
			leftValue = leftAnnot.getValue();
			rightValue = rightAnnot.getValue();
		} catch (ExprAnnotation.DynamicEvaluation ex) {
			throw new RuntimeException(ex);
		}
		
		if (leftValue == null) throw new RuntimeException("No left operand in concatenation");
		if (rightValue == null) throw new RuntimeException("No right operand in concatenation");
		
		if (! (leftValue instanceof String)) throw new RuntimeException("Left operand is not a string");
		if (! (rightValue instanceof String)) throw new RuntimeException("Right operand is not a string");
		
		String leftString = (String)leftValue;
		String rightString = (String)rightValue;
		
		String result = leftString + rightString;
		
		setExprAnnotation(node, result);
		*/
	}
	
	
	/* Utilities */
	
	String createNameFromPath(LinkedList<TId> path) {
		String name = "";
		boolean firstTime = true;
		for (TId id : path) {
			if (firstTime) firstTime = false;
			else name = name + ".";
			name = name + id.getText();
		}
		return name;
	}
}
