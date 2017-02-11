package scaledmarkets.dabl.main;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * A table of the symbol (identifier) declarations made within a given lexical
 * scope of a decl file.
 */
public class SymbolTable extends HashMap<String, SymbolEntry>
{
	private String name; // may be null.
	private NameScope scope; // thelexical scope in which this table's sympbols are defined.
	private SymbolTable parent; // may be null (for env table).
	private List<SymbolTable> children; // may be null.
	private SymbolTable nextTable; // allows tables to be chained together.
	
	public SymbolTable(NameScope scope, SymbolTable parent, List<SymbolTable> children)
	{
		this.scope = scope;
		this.parent = parent;
		if (children == null) this.children = new LinkedList<SymbolTable>();
		else this.children = children;
	}
	
	public SymbolTable(String name, NameScope scope, SymbolTable parent, List<SymbolTable> children)
	{
		this(scope, parent, children);
		this.name = name;
	}
	
	public NameScope getScope() { return scope; }
	
	public String getName()
	{
		if (name != null) return name;
		SymbolEntry selfEntry = this.scope.getSelfEntry();
		if (selfEntry != null) return selfEntry.getName();
		return null; // an anonymous scope
	}
	
	public SymbolTable getParent() { return parent; }
	
	public List<SymbolTable> getChildren() { return children; }
	
	public void addChildTable(SymbolTable child)
	{
		children.add(child);
	}
	
	/**
	 * Used to add a namespace to the current namespace.
	 */
	public SymbolTable appendTable(SymbolTable table)
	{
		if (nextTable != null) throw new RuntimeException(
			"Attempt to append a table to a table that already has an appended table");
		this.nextTable = table;
		return table;
	}
	
	public SymbolTable getNextTable()
	{
		return nextTable;
	}
	
	public SymbolEntry getEntry(String name)
	{
		SymbolEntry entry = get(name);
		if (entry != null) return entry;
		if (nextTable == null) return null;
		
		// Look in next table (recursive).
		return nextTable.getEntry(name);
	}
	
	public void addEntry(String name, SymbolEntry entry)
	throws SymbolEntryPresent // if there is already an entry with name 'name'.
	{
		if (get(name) != null) throw new SymbolEntryPresent(name);
		put(name, entry);
	}
	
	public NameScope getEnclosingScope(String name) throws SymbolNotPresent
	{
		SymbolEntry entry = get(name);
		if (entry == null) throw new SymbolNotPresent(name);
		return entry.getEnclosingScope();
	}
	
	
	/** diagnostic only */
	public void print()
	{
		String priorName = null;
		for (SymbolTable table = this; table != null; table = table.getNextTable())
		{
			if (priorName != null) System.out.print("(" + priorName + ".nextTable) ");
			String name = table.getName();
			if (name == null) System.out.println("<anonymous>");
			else System.out.println(name);
			for (String key : table.keySet())
			{
				System.out.println("\t" + key + ": " + table.get(key).getName());
			}
			priorName = name;
		}
	}
}
