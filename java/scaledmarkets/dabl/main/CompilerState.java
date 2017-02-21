package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.*;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.net.URLClassLoader;

/**
 * Contains the entire state of the compiler when processing an input file.
 * When the compiler finishes processing a file, it returns the CompilerState.
 */
public class CompilerState
{
	public List<Start> getASTs() { return this.asts; }
	
	public NameScope getGlobalScope() { return this.globalScope; }
	
    public Annotation getIn(Node node) { return in.get(node); }
    
    public Annotation getOut(Node node) { return out.get(node); }
	
    
	void setGlobalScope(NameScope scope) { this.globalScope = scope; }

	/**
	 * Root of the ASTs that are created. The first AST is for the main file;
	 * others are for imported namespaces.
	 */
	protected List<Start> asts = new LinkedList<Start>();
	
	/**
	 * Scope in which the namespace is defined.
	 */
	protected NameScope globalScope;
	
	/**
	 * A stack of NameScopes that is maintained during the Analysis phase.
	 * If Analysis completes without error, there will only be one NameScope
	 * in the stack: the global scope.
	 */
	protected List<NameScope> scopeStack = new LinkedList<NameScope>();
	
	/**
	 * AST Node annotations that are set on entry to the Node type's analysis
	 * method.
	 */
	protected Hashtable<Node, Annotation> in = new Hashtable<Node, Annotation>();
	
	/**
	 * AST Node annotations that are set on exit from the Node type's analysis
	 * method.
	 */
	protected Hashtable<Node, Annotation> out = new Hashtable<Node, Annotation>();

	protected void pushScope(NameScope scope) { this.scopeStack.add(0, scope); }
	
	protected NameScope popScope() {
		return this.scopeStack.remove(0);
	}
}
