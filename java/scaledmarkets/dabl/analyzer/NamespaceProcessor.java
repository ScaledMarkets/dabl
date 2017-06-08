package scaledmarkets.dabl.analyzer;

import java.io.Reader;
import java.io.PushbackReader;
import scaledmarkets.dabl.lexer.Lexer;
import scaledmarkets.dabl.parser.Parser;
import scaledmarkets.dabl.node.Start;

import sablecc.PrettyPrint;

/**
 * Parses and analyzes DABL source code. The source code is provided as input.
 */
public class NamespaceProcessor {
	
	private Analyzer analyzer;
	private boolean prettyPrint = false;
	
	public NamespaceProcessor(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public void setPrettyPrint(boolean yes) {
		this.prettyPrint = yes;
	}
	
	public boolean getPrettyPrint() {
		return this.prettyPrint;
	}
	
	/**
	 * 
	 */
	public NameScope processPrimaryNamespace(Reader reader) {
		
		NameScope nameScope = processNamespace(reader);
		this.analyzer.getState().setPrimaryNamespaceSymbolEntry(analyzer.getEnclosingScopeEntry());
		return nameScope;
	}
	
	/**
	 * 
	 */
	public NameScope processNamespace(Reader reader) {

		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		if (prettyPrint) PrettyPrint.pp(start);
		
		// Analyze the AST.
		analyzer.getState().getASTs().add(start);
		start.apply(this.analyzer);

		return analyzer.getNamespaceNamescope();
	}
}
