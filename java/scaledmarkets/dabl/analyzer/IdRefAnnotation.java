package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;

public class IdRefAnnotation implements Annotation
{
	private Node idRef;
	private SymbolEntry entry;
	
	public IdRefAnnotation(Node idRef, SymbolEntry entry)
	{
		this.idRef = idRef;
		this.entry = entry;
	}
	
	public Node getIdRef() { return idRef; }
	
	public SymbolEntry getDefiningSymbolEntry() { return entry; }
}
