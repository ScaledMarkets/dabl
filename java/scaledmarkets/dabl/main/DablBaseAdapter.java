package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

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

	public void assertThat(boolean expr)
	{
		if (! expr) throw new RuntimeException("Assertion failure");
	}
	
	public void assertThat(boolean expr, String msg)
	{
		if (! expr) throw new RuntimeException("Assertion failure: " + msg);
	}

	
	/* Scope management */
	
	protected NameScope getGlobalScope() { return state.globalScope; }
	
	protected NameScope pushNameScope(NameScope scope)
	{
		System.out.println("Pushing scope '" + scope.getName() + "'");  // debug
		state.scopeStack.add(0, scope);
		return scope;
	}
	
	protected NameScope popNameScope()
	{
		//(new RuntimeException("Popping scope '" + state.scopeStack.get(0).getName() + "'")).printStackTrace(System.out);  // debug
		return state.scopeStack.remove(0);
	}
	
	protected NameScope getCurrentNameScope()
	{
		if (state.scopeStack.size() == 0) return null;
		return state.scopeStack.get(0);
	}
	
	/**
	 * Find the entry in the symbol table that defines the specified id. The id
	 * is the last id on the specified path. Each id in the path represents the id
	 * of an enclosing scope. The current scope is first searched, and if the
	 * path is not found relative to the current scope, then the global scope
	 * is searched.
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
			"'" + theid.getText() + "' in " + pathToString(path) +
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
		assertThat(name != null);
		assertThat(initialScope != null);
		//System.out.println("Entered resolveSymbol(" + name + ", " +
		//	(initialScope == getGlobalScope() ? "global scope" : initialScope.getSelfEntry().getName()));
		
		for (NameScope scope = initialScope; scope != null; scope = scope.getParentNameScope())
		{
			SymbolTable curTable = scope.getSymbolTable();
			SymbolEntry entry = curTable.getEntry(name);
			if (entry != null) return entry;
		}
		return null;
	}
	
	/**
	 * 
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
		return (SymbolEntry)(getIn(id));
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
		this.setOut(node, pushNameScope(newScope));
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
		return (NameScope)(this.getIn(node));
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
	
	
	/* Utilities */

	public static String pathToString(List<TId> path)
	{
		String s = "";
		boolean firstTime = true;
		for (TId id : path)
		{
			if (firstTime) firstTime = false;
			else s += ".";
			s += id.getText();
		}
		return s;
	}
}
