package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;

public class IdRefAnnotation extends Annotation
{
	private AOidRef idRef;
	private SymbolEntry entry;
	
	public IdRefAnnotation(AOidRef idRef, SymbolEntry entry)
	{
		this.idRef = idRef;
		this.entry = entry;
	}
	
	public AOidRef getIdRef() { return idRef; }
	
	public SymbolEntry getDefiningSymbolEntry() { return entry; }
}
