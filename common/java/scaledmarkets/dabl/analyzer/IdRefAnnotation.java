package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.AOidRef;

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
