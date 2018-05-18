package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;
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
	
	/**
	 * Add the specified annotation ("o") to the set of "set-on-entry" attributes
	 * for the specified node.
	 */
    void setIn(Node node, Annotation a)
    {
    	if (getIn(node) != null) throw new RuntimeException(
    		"Attempt to replace an Attribute");
        if(a == null) this.in.remove(node);
        else this.in.put(node, a);
    }

    public Annotation getIn(Node node) { return in.get(node); }
    
	/**
	 * Add the specified annotation ("o") to the set of "set-on-exit" attributes
	 * for the specified node.
	 */
    void setOut(Node node, Annotation a)
    {
    	if (getOut(node) != null) throw new RuntimeException(
    		"Attempt to replace an existing Attribute " + getOut(node).getClass().getName() +
    		" with a " + a.getClass().getName() + ", on a " + node.getClass().getName());
        if(a == null) this.out.remove(node);
        else this.out.put(node, a);
    }
	
    public Annotation getOut(Node node) { return out.get(node); }
    
    public NameScopeEntry getPrimaryNamespaceSymbolEntry() { return primaryNamespaceSymbolEntry; }
	
	void setPrimaryNamespaceSymbolEntry(NameScopeEntry e) {
		primaryNamespaceSymbolEntry = e;
	}
	
	public SymbolEntry getIdBinding(TId id) {
		return (SymbolEntry)(this.getIn(id));
	}

	public NameScope getNameScope(Node node) {
		return (NameScope)(this.getIn(node));
	}

	void setGlobalScope(NameScope scope) { this.globalScope = scope; }

	/**
	 * The entry in the global scope that references the primary namespace.
	 */
	protected NameScopeEntry primaryNamespaceSymbolEntry;
	
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

	protected void pushScope(NameScope scope) {
		this.scopeStack.add(0, scope);
	}
	
	protected NameScope popScope() {
		return this.scopeStack.remove(0);
	}
	
	/**
	 * Switch the current scope stack with the specified one, and return the
	 * original scope stack.
	 */
	List<NameScope> swapScopeStack(List<NameScope> newScopeStack) {
		List<NameScope> originalScopeStack = scopeStack;
		this.scopeStack = newScopeStack;
		return originalScopeStack;
	}
	
	ExprAnnotation setExprAnnotation(Node node, Object value, ValueType valueType)
	{
		ExprAnnotation annotation = new ExprAnnotation(node, value, valueType);
		this.setOut(node, annotation);
		return annotation;
	}
	
	public ExprAnnotation getExprAnnotation(Node node) {
		return (ExprAnnotation)(this.getOut(node));
	}
}
