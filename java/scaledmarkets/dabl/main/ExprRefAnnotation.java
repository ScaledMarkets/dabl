package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;

public class ExprRefAnnotation extends ExprAnnotation
{
	private SymbolEntry entry;
	
	public ExprRefAnnotation(Node node, Object value, SymbolEntry entry)
	{
		super(node, value);
		this.entry = entry;
	}
	
	public SymbolEntry getDefiningSymbolEntry() { return entry; }
}
