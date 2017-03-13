package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.node.*;

/**
 * Annotation for an Id that is a declaration.
 */
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
