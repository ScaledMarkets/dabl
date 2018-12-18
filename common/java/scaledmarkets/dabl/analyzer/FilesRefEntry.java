package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.*;

/**
 * A FilesRefEntry is a named reference to a files declaration.
 */
public class FilesRefEntry extends DeclaredEntry
{
	private DeclaredEntry filesDeclEntry;
	
	public FilesRefEntry(String name, NameScope enclosingScope, Node definingNode,
		DeclaredEntry filesDeclEntry)
	{
		super(name, enclosingScope, definingNode);
		this.filesDeclEntry = filesDeclEntry;
	}
	
	public DeclaredEntry getFilesDeclEntry() { return this.filesDeclEntry; }
}
