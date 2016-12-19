package scaledmarkets.dabl.main;

import scaledmarkets.decl.node.*;

public class DeclaredEntry extends SymbolEntry
{
	private Node definingNode;
	
	public DeclaredEntry(TId id, NameScope enclosingScope, Node definingNode)
	{
		super(id, enclosingScope);
		this.definingNode = definingNode;
		if (definingNode == null) throw new RuntimeException();
	}
	
	public Node getDefiningNode() { return definingNode; }
}
