package scaledmarkets.dabl.analyzer;

public class ClientState extends CompilerState {
	
    public NameScopeEntry getPrimaryNamespaceSymbolEntry() { return primaryNamespaceSymbolEntry; }
	
	public ExprAnnotation getExprAnnotation(Node node) {
		return (ExprAnnotation)(this.getOut(node));
	}
    
	protected void setPrimaryNamespaceSymbolEntry(NameScopeEntry e) {
		primaryNamespaceSymbolEntry = e;
	}
	
	/**
	 * The entry in the global scope that references the primary namespace.
	 */
	protected NameScopeEntry primaryNamespaceSymbolEntry;
}
