package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.net.URLClassLoader;

public class CompilerState
{
	Start ast;
	NameScope globalScope;
	List<NameScope> scopeStack = new LinkedList<NameScope>();
	
	/**
	 * AST Node attributes that are set on entry to the Node type's analysis
	 * method.
	 */
	Hashtable<Node,Object> in = new Hashtable<Node,Object>();
	
	/**
	 * AST Node attributes that are set on exit from the Node type's analysis
	 * method.
	 */
	Hashtable<Node,Object> out = new Hashtable<Node,Object>();

	public CompilerState()
	{
	}
	
	void setGlobalScope(NameScope scope) { this.globalScope = scope; }
}
