package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.Config;

import sablecc.PrettyPrint;

import java.io.Reader;
import java.io.PushbackReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

/**
 * Wrapper for analyzer, making it convenient to call and providing options.
 * When embedding Dabl in other applications, instantiate this class.
 */
public class Dabl
{
	boolean print = false;
	boolean printTrace = false;
	boolean analysisOnly = false;
	
	Reader reader = null;
	ImportHandler importHandler = null;
	
	public Dabl(boolean print, boolean printTrace, Reader reader, ImportHandler importHandler) {
		this.print = print;
		this.printTrace = printTrace;
		this.reader = reader;
		this.importHandler = importHandler;
	}
	
	public Dabl(boolean print, boolean printTrace, Reader reader) {
		this(print, printTrace, reader, new DefaultImportHandler());
	}
	
	public Dabl(Reader reader) {
		this.reader = reader;
		this.importHandler = new DefaultImportHandler();
	}

	/**
	 * This is the actual DABL processor.
	 * The processing phases are described here:
	 	https://github.com/Scaled-Markets/dabl/tree/master/langref#processing-phases
	 */
	public CompilerState process() throws Exception {
		
		CompilerState state = new CompilerState();
		process(state);
		return state;
	}
	
	/**
	 * This version of process should only be called by ImportHandlers.
	 */
	public NameScope process(CompilerState state) throws Exception {
		
		// ....Need to insert template processor here
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(this.reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		System.out.println("Syntax is correct");
		state.getASTs().add(start);
		
		if (print) PrettyPrint.pp(start);
		
		// declarations. Expressions may be partially evaluated, where possible.
		// Values that depend on the DABL file context (e.g., its location on a
		// file system) are elaborated.
		LanguageAnalyzer analyzer = new LanguageAnalyzer(state, this.importHandler);
		start.apply(analyzer);
		
		return analyzer.getNamespaceNamescope();
	}
}
