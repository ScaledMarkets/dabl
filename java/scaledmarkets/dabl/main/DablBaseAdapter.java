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
	
    public void setIn(Node node, Object o)
    {
    	if (getIn(node) != null) throw new RuntimeException(
    		"Attempt to replace an Attribute");
        if(o == null) state.in.remove(node);
        else state.in.put(node, o);
    }

    public Object getIn(Node node) { return state.in.get(node); }

    public void setOut(Node node, Object o)
    {
    	if (getOut(node) != null) throw new RuntimeException(
    		"Attempt to replace an existing Attribute " + getOut(node).getClass().getName() +
    		" with a " + o.getClass().getName() + ", on a " + node.getClass().getName());
        if(o == null) state.out.remove(node);
        else state.out.put(node, o);
    }
	
    public Object getOut(Node node) { return state.out.get(node); }

	public void assertThat(boolean expr)
	{
		if (! expr) throw new RuntimeException("Assertion failure");
	}
	
	public void assertThat(boolean expr, String msg)
	{
		if (! expr) throw new RuntimeException("Assertion failure: " + msg);
	}

	
	private ClassLoader getProviderClassLoader() { return state.providerClassLoader; }
	
	
	/* Scope management */
	
	protected NameScope getGlobalScope() { return state.globalScope; }
	
	protected NameScope pushNameScope(NameScope scope)
	{
		state.scopeStack.add(0, scope);
		return scope;
	}
	
	protected NameScope popNameScope()
	{
		return state.scopeStack.remove(0);
	}
	
	protected NameScope getCurrentNameScope() { return state.scopeStack.get(0); }
	
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
		enclosingScope.getSymbolTable().addEntry(id, entry);
		this.setOut(id, entry);
		return entry;
	}
	
	protected SymbolEntry getIdBinding(TId id)
	{
		return (SymbolEntry)(getOut(id));
	}
	
	/**
	 * Create a NameScope Annotation for the specified Node.
	 */
	protected NameScope createNameScope(Node node)
	{
		NameScope newScope = new NameScope(node, getCurrentNameScope());
		this.setOut(node, pushNameScope(newScope));
		return newScope;
	}
	
	protected NameScope getNameScope(Node node)
	{
		return (NameScope)(this.getOut(node));
	}
	
	/**
	 * Create an ExprAnnotation for the specified Node, and also set the value
	 * for the expression in the Annotation. (This method must be called by
	 * an out... method, so that the value will be available.)
	 */
	protected ExprAnnotation setExprAnnotation(POexpr node, Object value)
	{
		ExprAnnotation annotation = new ExprAnnotation(node, value);
		this.setOut(node, annotation);
		return annotation;
	}
	
	/**
	 * An expression whose value is defined in the declaration of a symbol.
	 */
	protected ExprRefAnnotation setExprRefAnnotation(POexpr node, Object value,
		SymbolEntry entry)
	{
		ExprRefAnnotation annotation = new ExprRefAnnotation(node, value, entry);
		this.setOut(node, annotation);
		return annotation;
	}
	
	/**
	 * Use this method instead of setExprAnnotation when the value cannot be
	 * computed statically because at least one method in the expression must
	 * be computed at runtime.
	 */
	protected ExprAnnotation setExprDynamic(POexpr node)
	{
		return setExprAnnotation(node, new ExprAnnotation.DynamicValuePlaceholder());
	}
	
	protected ExprAnnotation getExprAnnotation(POexpr node)
	{
		Object obj = this.getOut(node);
		if (obj == null) return null;
		if (! (obj instanceof ExprAnnotation)) throw new RuntimeException(
			"Unexpected: Annotation is of wrong type: " + obj.getClass().getName());
		return (ExprAnnotation)obj;
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
