package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.Config;

import sablecc.PrettyPrint;

import java.io.Reader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
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
	private boolean print = false;
	private boolean printTrace = false;
	private boolean analysisOnly = false;
	
	private Reader reader = null;
	private ImportHandler importHandler = null;
	
	public Dabl(boolean print, boolean printTrace, Reader reader, ImportHandler importHandler) {
		this.print = print;
		this.printTrace = printTrace;
		this.reader = reader;
		this.importHandler = importHandler;
	}
	
	public Dabl(boolean print, boolean printTrace, Reader reader) {
		this(print, printTrace, reader, new FileImportHandler(new DablNamespaceProcessor()));
	}
	
	public Dabl(Reader reader) {
		this.reader = reader;
		this.importHandler = new FileImportHandler(new DablNamespaceProcessor());
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
		
		
		LanguageAnalyzer analyzer = new LanguageAnalyzer(state, this.importHandler);
		NamespaceProcessor processor = new DablNamespaceProcessor(analyzer);
		
		if (print) PrettyPrint.pp(start);
		
		Reader reader = new StringReader(DablStandard.PackageText);
		NameScope scope = analyzer.analyzeNamespace(reader, state);

		state.setPrimaryNamespaceSymbolEntry(analyzer.getEnclosingScopeEntry());
		
		return scope;
	}
}
