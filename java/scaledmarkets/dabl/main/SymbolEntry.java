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
 * A symbol table entry. Each entry defines a symbol (identifer) that was declared
 * in the decl file.
 */
public abstract class SymbolEntry
{
	private TId id;
	private NameScope enclosingScope;
	
	public SymbolEntry(TId id, NameScope enclosingScope)
	{
		this.id = id;
		if (id == null) throw new RuntimeException();
		this.enclosingScope = enclosingScope;  // the scope in which the id is defined.
	}
	
	public TId getId() { return id; }
	
	public String getName() { return id.getText(); }
	
	public NameScope getEnclosingScope() { return enclosingScope; }
	
	public String toString()
	{
		return this.getClass().getName() + ": name=" + id.getText() + ", enclosingScope=" + enclosingScope.toString();
	}
}
