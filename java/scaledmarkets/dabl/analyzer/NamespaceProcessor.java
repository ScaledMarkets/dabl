package scaledmarkets.dabl.analyzer;

import java.io.Reader;
import java.io.PushbackReader;
import scaledmarkets.dabl.lexer.Lexer;
import scaledmarkets.dabl.parser.Parser;
import scaledmarkets.dabl.node.Start;

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
		Start start;
		try { start = parser.parse(); } catch (Exception ex) { throw new RuntimeException(ex); }
		if (prettyPrint) PrettyPrint.pp(start);
		
		// Analyze the AST.
		analyzer.getState().getASTs().add(start);
		start.apply(this.analyzer);

		return analyzer.getNamespaceNamescope();
	}
}
