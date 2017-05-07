package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.util.Utilities;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

/**
 * Add methods for name resolution, symbol table creation and management, and
 * Node annotation.
 */
public abstract class DablBaseAdapter extends DepthFirstAdapter
{
	private CompilerState state;
	private List<IdentHandler> identHandlers = new LinkedList<IdentHandler>();
	
	public DablBaseAdapter(CompilerState state)
	{
		this.state = state;
	}
	
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
    	if (getIn(node) != null) throw new RuntimeException(
    		"Attempt to replace an Attribute");
        if(a == null) state.in.remove(node);
        else state.in.put(node, a);
    }

    public Annotation getIn(Node node) { return state.in.get(node); }

	/**
	 * Add the specified annotation ("o") to the set of "set-on-exit" attributes
	 * for the specified node.
	 */
    public void setOut(Node node, Annotation a)
    {
    	if (getOut(node) != null) throw new RuntimeException(
    		"Attempt to replace an existing Attribute " + getOut(node).getClass().getName() +
    		" with a " + a.getClass().getName() + ", on a " + node.getClass().getName());
        if(a == null) state.out.remove(node);
        else state.out.put(node, a);
    }
	
    public Annotation getOut(Node node) { return state.out.get(node); }

	
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
	 * 
	 */
    protected void outRefNode(Node node, List<TId> path, VisibilityChecker checker)
    {
		// Find the declaration of the id. If it exists, annotate it.
		
		SymbolEntry entry = resolveSymbol(path);
		if (entry == null) {
			// Might be a reference to something that is declared later.
			// 
			// Identify all the enclosing scopes, and attach a handler to each one.
			// The handler should be invoked whenever a new symbol is entered
			// into a scope. The handler should annotate the entry,
			// and then remove itself from all of the scopes to which it is attached.
			new IdentHandler(this, path, getCurrentNameScope()) {
				public void resolveRetroactively(DeclaredEntry entry) {
					checker.check(getCurrentNameScope(), entry);
					setIdRefAnnotation(node, entry);
				}
				// Note: the base class, IdentHandler, contains a method
				// checkForPathResolution, which calls resolveRetroactively, 
				// followed by removeFromAllScopes().
			};
			
		} else {
			// Annotate the Id reference with the DeclaredEntry that defines the Id.
			if (! (entry instanceof DeclaredEntry)) throw new RuntimeException(
				"Unexpected: entry is a " + entry.getClass().getName());
			checker.check(getCurrentNameScope(), entry);
			DeclaredEntry declent = (DeclaredEntry)entry;
			setIdRefAnnotation(node, declent);
		}
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
		Utilities.assertThat(name != null, "symbol name is null");
		Utilities.assertThat(initialScope != null, "initial scope is null");
		
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
	 * This method should be called after each name declaration is processed.
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
	protected IdRefAnnotation setIdRefAnnotation(Node idRef, SymbolEntry entry)
	{
		IdRefAnnotation annotation = new IdRefAnnotation(idRef, entry);
		this.setOut(idRef, annotation);
		return annotation;
	}
	
	protected ExprAnnotation setExprAnnotation(Node node, Object value)
	{
		ExprAnnotation annotation = new ExprAnnotation(node, value);
		this.setOut(node, annotation);
		return annotation;
	}
	
	protected ExprAnnotation getExprAnnotation(Node node)
	{
		return this.state.getExprAnnotation(node);
	}
}
