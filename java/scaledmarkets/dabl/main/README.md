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
	
	DablBaseAdapter (extends DepthFirstAdapter) - 
	
	SymbolTable (a HashMap<String, SymbolEntry>) - 
	
	SymbolEntry
	DeclaredEntry (extends SymbolEntry) - 
	NameScopeEntry (extends DeclaredEntry) - 
	
	
	Annotation
	
	NameScope (extends Annotation) - All Axxx classes that define a lexical scope
		should be annotated with this. A NameScope contains a SymbolTable.
	
	ExprAnnotation (extends Annotation) - 
	
	ExprRefAnnotation (extends ExprAnnotation) - 
	
	IdentHandler - An IdentHandler is attached to enclosing scopes when a symbol
		is not recognized
		but might be defined later in an enclosing scope. Later, when the symbol is
		defined, attached Handlers are checked to see if any refer to the symbol. If so,
		the Handler's resolveRetroactively method is called, to resolve the original
		symbol reference.


</dl>

