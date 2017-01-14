package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;

public class DeclaredEntry extends SymbolEntry
{
	private Node definingNode;
	
	public DeclaredEntry(String name, NameScope enclosingScope, Node definingNode)
	{
		super(name, enclosingScope);
		this.definingNode = definingNode;
		if (definingNode == null) throw new RuntimeException();
	}
	
	public Node getDefiningNode() { return definingNode; }
}
