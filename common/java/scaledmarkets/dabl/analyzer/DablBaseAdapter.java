package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.analysis.*;
import com.scaledmarkets.dabl.util.Utilities;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 * Add methods for name resolution, symbol table creation and management, and
 * Node annotation. These methods are mostly language-agnostic (but may reflect
 * some particular needs for a language such as DABL).
 */
public abstract class DablBaseAdapter extends DepthFirstAdapter implements Analyzer
{
	private CompilerState state;
	private List<IdentHandler> identHandlers = new LinkedList<IdentHandler>();
	private Map<Node, IdentSemanticHandler> identSemanticHandlers = new HashMap<Node, IdentSemanticHandler>();
	
	DablBaseAdapter(CompilerState state)
	{
		this.state = state;
	}
	
	public abstract ImportHandler getImportHandler();
	
	public abstract NameScopeEntry getEnclosingScopeEntry();
	
	public CompilerState getState() { return state; }
	
	public void addIdentHandler(IdentHandler handler) { identHandlers.add(handler); }
	
	public void removeIdentHandler(IdentHandler handler) { identHandlers.remove(handler); }

	public List<IdentHandler> getIdentHandlers() { return identHandlers; }
	
	/**
	 * Add the specified annotation ("o") to the set of "set-on-entry" attributes
	 * for the specified node.
	 */
    public void setIn(Node node, Annotation a)
    {
    	state.setIn(node, a);
    }

    public Annotation getIn(Node node) { return state.getIn(node); }

    public void setOut(Node node, Annotation a)
    {
    	state.setOut(node, a);
    }
	
    public Annotation getOut(Node node) { return state.getOut(node); }

	
	/* Scope management */
	
	protected NameScope getGlobalScope() { return state.globalScope; }
	
	protected NameScope pushNameScope(NameScope scope)
	{
		state.pushScope(scope);
		return scope;
	}
	
	protected NameScope popNameScope()
	{
		return state.popScope();
	}
	
	protected NameScope getCurrentNameScope()
	{
		if (state.scopeStack.size() == 0) return null;
		return state.scopeStack.get(0);
	}
	
	/**
	 * Resolve a reference to a name that is declared elsewhere.
	 */
    protected void outRefNode(AOidRef idRef, List<TId> path, VisibilityChecker checker)
    {
		// Find the declaration of the id. If it exists, annotate it.
		
		SymbolEntry entry = resolveSymbol(path);
		if (entry == null) {  // it is currently undeclared.
			// Might be a reference to something that is declared later.
			// 
			// Identify all the enclosing scopes, and attach a handler to each one.
			// The handler should be invoked whenever a new symbol is entered
			// into a scope. The handler should annotate the entry,
			// and then remove itself from all of the scopes to which it is attached.
			new IdentHandler(DablBaseAdapter.this, path, getCurrentNameScope()) {
				public void resolveRetroactively(DeclaredEntry entry) {
					DablBaseAdapter.this.resolve(idRef, checker, entry);
					
					// If there is a semantic handler, execute it.
					IdentSemanticHandler h = getIdentSemanticHandler(idRef);
					if (h != null) h.semanticAction(entry);
				}
				// Note: the base class, IdentHandler, contains a method
				// checkForPathResolution, which calls resolveRetroactively, 
				// followed by removeFromAllScopes().
			};
			
		} else {  // declaration found.
			// Annotate the Id reference with the DeclaredEntry that defines the Id.
			if (! (entry instanceof DeclaredEntry)) throw new RuntimeException(
				"Unexpected: entry is a " + entry.getClass().getName());
			resolve(idRef, checker, (DeclaredEntry)entry);
		}
	}
	
	/**
	 * Register the specified semantic handler for the specified node. The node must
	 * be a symbol reference - specifically a AOidRef.
	 * A semantic handlers for a symbol reference is invoked when the symbol reference
	 * is matched with the symbol declaration. Only one semantic handler can be
	 * registered for a given node.
	 */
	public void registerSemanticHandlerFor(AOidRef ref, IdentSemanticHandler handler) {
		
    	assertThat(this.identSemanticHandlers.get(ref) == null,
    		"A semantic handler for node '" + ref.toString() + "' is already registered");
		this.identSemanticHandlers.put(ref, handler);
	}

	/**
	 * Perform semantic actions when the specified symbol reference is evantually
	 * declared. Semantic handlers are invoked when a declaration is recognized
	 * and after the symbol reference has been matched up with the symbol declaration.
	 * See the outRefNode method in DablBaseAdapter.
	 */
	public abstract class IdentSemanticHandler {
		private AOidRef ref;
		
		public IdentSemanticHandler(AOidRef ref) {
			this.ref = ref;
			DablBaseAdapter.this.registerSemanticHandlerFor(ref, this);
		}
		
		public abstract void semanticAction(DeclaredEntry entry);
	}
	
	/**
	 * Return the semantic handler for the specified symbol reference, or null if
	 * there is not one.
	 */
	protected IdentSemanticHandler getIdentSemanticHandler(AOidRef node) {
		
    	return this.identSemanticHandlers.get(node);
	}
	
	/**
	 * Annotate the node with the DeclaredEntry that defines the Id.
	 */
	void resolve(AOidRef node, VisibilityChecker checker, DeclaredEntry entry) {
		checker.check(getCurrentNameScope(), entry);
		setIdRefAnnotation(node, entry);
	}
    
	/**
	 * Find the entry in the symbol table that defines the specified id. The id
	 * is the last id on the specified path. Each id in the path represents the id
	 * of an enclosing scope. The current scope is first searched, and if the
	 * path is not found relative to the current scope, then the global scope
	 * is searched. Return null if not found.
	 */
	protected SymbolEntry resolveSymbol(List<TId> path)
	{
		return resolveSymbol(path, getCurrentNameScope());
	}

	protected SymbolEntry resolveSymbol(List<TId> path, NameScope scope)
	{
		SymbolEntry entry = null;
		boolean firstTime = true;
		SymbolTable curTable = null;
		int count = 0;
		TId theid = null;
		
		for (TId id : path)
		{
			theid = id;
			count++;
			if (firstTime)
			{
				firstTime = false;
				entry = resolveSymbol(id, scope);
			}
			else
			{
				entry = curTable.getEntry(id.getText());
				if (entry == null) return null;
			}
			
			if (! (entry instanceof NameScopeEntry)) break;
			curTable = ((NameScopeEntry)entry).getOwnedScope().getSymbolTable();
				// table owned by the entry's NameScope.
				// (The entry must refer to a NameScopeEntry.)
		}
		
		if (count < path.size()) throw new RuntimeException(
			"'" + theid.getText() + "' in " + Utilities.createNameFromPath(path) +
			" is not the last element of the path, and it does" +
			" not define a scope.");
		else
			return entry;
	}
	
	protected SymbolEntry resolveSymbol(TId id, NameScope scope)
	{
		// Search from the current scope.
		String name = id.getText();
		return resolveSymbol(name, scope);
	}
	
	protected SymbolEntry resolveSymbol(TId id)
	{
		return resolveSymbol(id, getCurrentNameScope());
	}
	
	protected SymbolEntry resolveSymbol(String name)
	{
		return resolveSymbol(name, getCurrentNameScope());
	}
	
	protected SymbolEntry resolveSymbol(String name, NameScope initialScope)
	{
		assertThat(name != null, "symbol name is null");
		assertThat(initialScope != null, "initial scope is null");
		
		for (NameScope scope = initialScope; scope != null; scope = scope.getParentNameScope())
		{
			SymbolTable curTable = scope.getSymbolTable();
			SymbolEntry entry = curTable.getEntry(name);
			if (entry != null) return entry;
		}
		return null;
	}
	
	/**
	 * Beginning with the current scope, and progressing outward, attempt to
	 * resolve the unresolved references that are attached to each scope.
	 * This method should be called after each name declaration is processed,
	 * so that references to that name are then resolved.
	 */
	protected void resolveForwardReferences(DeclaredEntry entry)
	{
		for (NameScope scope = getCurrentNameScope();
			scope != null; scope = scope.getParentNameScope())
		{
			scope.resolveForwardReferences(entry);
		}
	}

	
	/* Methods for annotating */
	
	protected SymbolEntry addSymbolEntry(SymbolEntry entry, TId id, NameScope enclosingScope)
	throws SymbolEntryPresent
	{
		enclosingScope.getSymbolTable().addEntry(id.getText(), entry);
		this.setIn(id, entry);
		return entry;
	}
	
	protected SymbolEntry addSymbolEntry(SymbolEntry entry, TId id)
	throws SymbolEntryPresent
	{
		return addSymbolEntry(entry, id, getCurrentNameScope());
	}
	
	protected SymbolEntry getIdBinding(TId id)
	{
		return this.state.getIdBinding(id);
	}
	
	/**
	 * Create a NameScope Annotation for the specified Node.
	 */
	protected NameScope createNameScope(String name, Node node)
	{
		NameScope newScope = new NameScope(name, node, getCurrentNameScope());
		this.setIn(node, pushNameScope(newScope));
		return newScope;
	}
	
	protected NameScope createNameScope(Node node)
	{
		NameScope newScope = new NameScope(node, getCurrentNameScope());
		this.setIn(node, pushNameScope(newScope));
		return newScope;
	}
	
	/**
	 * For declarations that define both an Id and a NameScope.
	 */
	protected NameScope createNameScope(TId id, Node node)
	{
		NameScope curScope = getCurrentNameScope();
		NameScope newNameScope = createNameScope(node);
		if (id != null) try
		{
			NameScopeEntry nameScopeEntry =
				new NameScopeEntry(newNameScope, id.getText(), curScope);
			addSymbolEntry(nameScopeEntry, id, curScope);
			newNameScope.setSelfEntry(nameScopeEntry);
			resolveForwardReferences(nameScopeEntry);
		}
		catch (SymbolEntryPresent ex) { throw new RuntimeException(ex); }
		return newNameScope;
	}

	protected NameScope getNameScope(Node node)
	{
		return this.state.getNameScope(node);
	}
	
	/**
	 * An expression whose value is defined in the declaration of a symbol.
	 */
	protected IdRefAnnotation setIdRefAnnotation(AOidRef idRef, SymbolEntry entry)
	{
		IdRefAnnotation annotation = new IdRefAnnotation(idRef, entry);
		this.setOut(idRef, annotation);
		return annotation;
	}
	
	protected ExprAnnotation setExprAnnotation(Node node, Object value, ValueType valueType)
	{
		return getState().setExprAnnotation(node, value, valueType);
	}
	
	public ExprAnnotation getExprAnnotation(Node node) {
		return getState().getExprAnnotation(node);
	}
	
	public void assertThat(boolean expr, String msg) {
		Utilities.assertThat(expr, msg);
	}
	
	public void assertThat(boolean expr, Runnable action) {
		Utilities.assertThat(expr, action);
	}
}
