package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;

/**
 * A LocalRepoEntry declared a local repository for DABL to create. Local repositories
 * contain intermediate build artifacts.
 */
public class LocalRepoEntry extends DeclaredEntry
{
	public LocalRepoEntry(String name, NameScope enclosingScope, Node definingNode)
	{
		super(name, enclosingScope, definingNode);
	}
}
