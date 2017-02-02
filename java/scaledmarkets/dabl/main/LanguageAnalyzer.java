package scaledmarkets.dabl.main;

//import scaledmarkets.dabl.analysis.*;
//import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
//import scaledmarkets.dabl.parser.*;

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
	
	public LanguageAnalyzer(CompilerState state, ImportHandler importHandler) {
		super(state);
		this.importHandler = importHandler;
		
		state.setGlobalScope(pushNameScope(new NameScope("Global", null, null)));
		assertThat(state.scopeStack.size() == 1);   // debug
	}
	
	protected ImportHandler getImportHandler() { return importHandler; }
	
	
	/* Resolve references to declared names. */
	
    public void inAOidRef(AOidRef node)
    {
        defaultIn(node);
    }

    public void outAOidRef(AOidRef node)
    {
		// Find the declaration of the id, and see if it is defined to have
		// a value. If so, attribute this node with the same value.
		TId id = node.getId();
		LinkedList<TId> path = new LinkedList<TId>();
		path.add(id);
		SymbolEntry entry = resolveSymbol(path);
		if (entry == null)
		{
			// Might be a reference to something that is declared later.
			// 
			// Identify all the enclosing scopes, and attach a handler to each one.
			// The handler should be invoked whenever a new symbol is entered
			// into a scope. The handler should,
			// 1. Perform custom logic, defined here (in outAOidRef).
			// 2. If the logic succeeds, the handler should remove itself from
			// all of the scopes to which it is attached.
			new IdentHandler(this, path, getCurrentNameScope()) {
				final ....AIdentExpr idExpr = node;
				public void resolveRetroactively(DeclaredEntry entry)
				{
					if (entry instanceof ConstantEntry)
					{
						// Has an expression that might have a value.
						ConstantEntry constEntry = (ConstantEntry)entry;
						....PExpr declExpr = constEntry.getExpression();
						ExprAnnotation declAnnot = getExprAnnotation(declExpr);
						Object value = null;
						try { value = declAnnot.getValue(); }
						catch (DynamicEvaluation ex) { value = new DynamicValuePlaceholder(); }
						setExprRefAnnotation(idExpr, value, entry);
					}
					else
						throw new RuntimeException(
							entry.getName() + " does not have a value, at line " +
							entry.getId().getLine() + ", pos " + entry.getId().getPos());
				}
			};
			return;
		}
		
		if (! (entry instanceof DeclaredEntry)) throw new RuntimeException(
			"Unexpected: entry is a " + entry.getClass().getName());
		DeclaredEntry declent = (DeclaredEntry)entry;
		annotateIdentExpr(declent);
    }
	
	
	/* Resolve forward references. */
	
	....
		resolveForwardReferences(nameScopeEntry);
	
	
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
	}
	
	public void outAOnamespace(AOnamespace node) {
		popNameScope();
	}
	
	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		
		String name = createNameFromPath(node.getId());
		NameScope importedScope = getImportHandler().importNamespace(name);
		
		getState().globalScope.getSymbolTable().appendTable(importedScope.getSymbolTable());
	}
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		
		String name = node.getName().getText();
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = new NameScope(name, node, enclosingScope);
		SymbolEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		pushNameScope(newScope);
		resolveForwardReferences(entry);
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
		setExprAnnotation(node, value);
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
		setExprAnnotation(node, value);
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
