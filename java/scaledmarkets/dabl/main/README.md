# Compiler Design

## Language Analyzer

The LanguageAnalyzer class performs the Analysis processing phase
(see https://github.com/Scaled-Markets/dabl/tree/master/langref#processing-phases).
It uses the following:

<dl>
	<dd>Compiler state - </dd>
	<dd><dl>
		<dd>ast - </dd>
		<dd>globalScope - </dd>
		<dd>scopeStack - </dd>
		<dd>in - </dd>
		<dd>out - </dd>
	</dl></dd>
	
	<dd>NameScope</dd>
	
	<dd>DablBaseAdapter (extends DepthFirstAdapter) - Base class for LanguageAnalyzer.
		Key methods include:
		<dl>
		<dd>addSymbolEntry(SymbolEntry entry, TId id, NameScope enclosingScope) -
			Adds the specified symbol table entry for the specified Id to
			the specified name scope.</dd>
		<dd>createNameScope(Node node) - Create a new NameScope within the current
			NameScope, push the new NameScope on the scope stack, and annotate
			the specified Node with the new NameScope.</dd>
		<dd>setExprAnnotation(POexpr node, Object value) - Annotate the specified
			POExpr node with a new ExprAnnotation.</dd>
		<dd>setExprRefAnnotation(POexpr node, Object value, SymbolEntry entry) - 
			Annotate the specified POexpr node with a new ExprRefAnnotation.</dd>
		</dl>
		
	<dd>SymbolTable (a HashMap<String, SymbolEntry>) - Self explanatory.</dd>
	
	<dd>SymbolEntry (abstract) - All symbol table entries are of a derived type.</dd>
	
	<dd>DeclaredEntry (extends SymbolEntry) - A symbol that is defined in a declaration.</dd>
	
	<dd>NameScopeEntry (extends DeclaredEntry) - A DeclaredEntry that defines
		a lexical name scope.</dd>
	
	<dd>Annotation (abstract) - Base type for all AST node annotations.</dd>
	
	<dd>NameScope (extends Annotation) - All Axxx classes that define a lexical scope
		should be annotated with this. A NameScope contains a SymbolTable.</dd>
	
	<dd>ExprAnnotation (extends Annotation) - An annotation for a expression node.</dd>
	
	<dd>ExprRefAnnotation (extends ExprAnnotation) - For expressions whose value
		is defined in the declaration of a symbol.</dd>
	
	<dd>IdentHandler - An IdentHandler is attached to enclosing scopes when a symbol
		is not recognized but might be defined later in an enclosing scope. Later,
		when the symbol is defined, attached Handlers are checked to see if any
		refer to the symbol. If so, the Handler's resolveRetroactively method
		is called, to resolve the original symbol reference.</dd>

</dl>

