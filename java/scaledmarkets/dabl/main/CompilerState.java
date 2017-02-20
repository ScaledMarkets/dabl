package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.net.URLClassLoader;

public class CompilerState
{
	/**
	 * Root of the ASTs that are created. The first AST is for the main file;
	 * others are for imported namespaces.
	 */
	public List<Start> asts = new LinkedList<Start>();
	
	/**
	 * Scope in which the namespace is defined.
	 */
	public NameScope globalScope;
	
	/**
	 * A stack of NameScopes that is maintained during the Analysis phase.
	 * If Analysis completes without error, there will only be one NameScope
	 * in the stack: the global scope.
	 */
	public List<NameScope> scopeStack = new LinkedList<NameScope>();
	
	/**
	 * AST Node annotations that are set on entry to the Node type's analysis
	 * method.
	 */
	public Hashtable<Node, Annotation> in = new Hashtable<Node, Annotation>();
	
	/**
	 * AST Node annotations that are set on exit from the Node type's analysis
	 * method.
	 */
	public Hashtable<Node, Annotation> out = new Hashtable<Node, Annotation>();

	public CompilerState()
	{
	}
	
	public void pushScope(NameScope scope) { this.scopeStack.add(0, scope); }
	
	public NameScope popScope() {
		return this.scopeStack.remove(0);
	}
	
	public void setGlobalScope(NameScope scope) { this.globalScope = scope; }

    public Annotation getIn(Node node) { return in.get(node); }
    
    public Annotation getOut(Node node) { return out.get(node); }
}
