package scaledmarkets.dabl.main;

import scaledmarkets.decl.node.*;

public class ConstantEntry extends DeclaredEntry
{
	public ConstantEntry(TId id, NameScope enclosingScope, AConststmt definingNode)
	{
		super(id, enclosingScope, definingNode);
	}
	
	public PExpr getExpression() { return ((AConststmt)(getDefiningNode())).getExpr(); }
}
