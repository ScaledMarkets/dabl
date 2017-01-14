package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

/**
 * All Axxx classes that define a lexical scope should be annotated with this.
 * A NameScope contains a SymbolTable.
 */
public class NameScope extends Annotation
{
	private Node nodeThatDefinesScope;
	private NameScope parentScope;
	private SymbolTable symbolTable;
	
	/**
	 * the symbol table entry for this scope's name.
	 * Note: The scopes that do not have a self entry are top-level scopes and
	 * anonymous scopes.
	 */
	private NameScopeEntry selfEntry = null;
	
	private Map<TId, IdentHandler> identHandlers = new HashMap<TId, IdentHandler>();
	
	public NameScope(Node nodeThatDefinesScope, NameScope parentScope)
	{
		this(null, nodeThatDefinesScope, parentScope);
	}
	
	public NameScope(String name, Node nodeThatDefinesScope, NameScope parentScope)
	{
		this.nodeThatDefinesScope = nodeThatDefinesScope;
		this.parentScope = parentScope;
		SymbolTable parentTable = (parentScope == null ? null : parentScope.getSymbolTable());
		this.symbolTable = new SymbolTable(name, this, parentTable, null);
		if (parentTable != null) parentTable.addChildTable(this.symbolTable);
	}
	
	/**
	 * Returned Node is one of the Node types labeled as 'a NameScope' in decl.sablecc.
	 */
	public Node getNodeThatDefinesScope() { return nodeThatDefinesScope; }
	
	public NameScope getParentNameScope() { return parentScope; }
	
	public SymbolTable getSymbolTable() { return symbolTable; }
	
	public SymbolEntry getEntry(String name) { return symbolTable.getEntry(name); }
	
	public void addEntry(String name, SymbolEntry entry)
	throws SymbolEntryPresent
	{
		symbolTable.addEntry(name, entry);
	}
	
	public String getName() { return symbolTable.getName(); }  // may be null
	
	/**
	 * Save reference to the entry that parent scope's symbol table has for this
	 * name scope. Thus, this is the symbol table entry for this scope's name.
	 */
	public NameScopeEntry setSelfEntry(NameScopeEntry entry)
	{
		return this.selfEntry = entry;
	}
	
	public NameScopeEntry getSelfEntry() { return selfEntry; }
	
	public void addIdentHandler(TId id, IdentHandler handler)
	{
		identHandlers.put(id, handler);
	}
	
	public void removeIdentHandler(TId id) { identHandlers.remove(id); }
	
	public void resolveForwardReferences(DeclaredEntry entry)
	{
		Collection<IdentHandler> handlerList = identHandlers.values();
		for (IdentHandler handler : new LinkedList<IdentHandler>(handlerList))
		{
			handler.checkForPathResolution(entry);
		}
	}
}
