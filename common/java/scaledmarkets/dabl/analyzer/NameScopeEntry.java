package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.*;

/**
 * A DeclaredEntry that refers to a NameScope. I.e., the name of the entry is
 * a named declarative region. NameScopeEntries must be created by the
 * Analyzer whenever it encounters an Id that defines a scope. NameScopeEntries
 * always refer to Ids.
 */
public class NameScopeEntry extends DeclaredEntry
{
	private NameScope ownedScope;
	
	public NameScopeEntry(NameScope ownedScope, String name, NameScope enclosingScope)
	{
		super(name, enclosingScope, ownedScope.getNodeThatDefinesScope());
		this.ownedScope = ownedScope;
	}
	
	public NameScope getOwnedScope() { return this.ownedScope; }
}
