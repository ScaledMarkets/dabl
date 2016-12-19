package scaledmarkets.dabl.main;

import scaledmarkets.decl.node.*;
import scaledmarkets.decl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.net.URLClassLoader;

public class CompilerState
{
	Hashtable<Node,Object> in = new Hashtable<Node,Object>();
	Hashtable<Node,Object> out = new Hashtable<Node,Object>();
	List<NameScope> scopeStack = new LinkedList<NameScope>();
	NameScope globalScope;
	URLClassLoader providerClassLoader;

	public CompilerState()
	{
	}
	
	void setGlobalScope(NameScope scope) { this.globalScope = scope; }
	
	void setProviderClassLoader(URLClassLoader loader) { this.providerClassLoader = loader; }
}
