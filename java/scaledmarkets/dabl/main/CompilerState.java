package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.net.URLClassLoader;

public class CompilerState
{
	public Start ast;
	public NameScope globalScope;
	public List<NameScope> scopeStack = new LinkedList<NameScope>();
	
	/**
	 * AST Node attributes that are set on entry to the Node type's analysis
	 * method.
	 */
	public Hashtable<Node,Object> in = new Hashtable<Node,Object>();
	
	/**
	 * AST Node attributes that are set on exit from the Node type's analysis
	 * method.
	 */
	public Hashtable<Node,Object> out = new Hashtable<Node,Object>();

	public CompilerState()
	{
	}
	
	void setGlobalScope(NameScope scope) { this.globalScope = scope; }

    public Object getOut(Node node) { return out.get(node); }
}
