package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.node.*;

public class IdRefAnnotation implements Annotation
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
