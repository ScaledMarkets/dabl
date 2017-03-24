package scaledmarkets.dabl.analysis;

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
 * in the dabl file.
 */
public abstract class SymbolEntry implements Annotation
{
	private String name;
	private NameScope enclosingScope;
	private boolean declaredPublic = false;
	
	public SymbolEntry(String name, NameScope enclosingScope)
	{
		this.name = name;
		if (name.equals("")) throw new RuntimeException("Empty name");
		this.enclosingScope = enclosingScope;  // the scope in which the id is defined.
	}
	
	public String getName() { return name; }
	
	public NameScope getEnclosingScope() { return enclosingScope; }
	
	public boolean isDeclaredPublic() { return declaredPublic; }
	
	protected void setDeclaredPublic() { declaredPublic = true; }
	
	public String toString()
	{
		return this.getClass().getName() + ": name=" + name + ", enclosingScope=" + enclosingScope.toString();
	}
}
