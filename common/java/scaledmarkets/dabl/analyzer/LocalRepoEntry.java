package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.*;

/**
 * A LocalRepoEntry declares a local repository for DABL to create. Local repositories
 * contain intermediate build artifacts.
 */
public class LocalRepoEntry extends DeclaredEntry
{
	public LocalRepoEntry(String name, NameScope enclosingScope, Node definingNode)
	{
		super(name, enclosingScope, definingNode);
	}
}
