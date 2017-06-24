package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.Config;

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
	
	public Dabl(boolean print, boolean printTrace, Reader reader) {
		this.print = print;
		this.printTrace = printTrace;
		this.reader = reader;
	}
	
	public Dabl(Reader reader) {
		this.reader = reader;
	}

	/**
	 * This is the actual DABL processor.
	 * The processing phases are described here:
	 	https://github.com/Scaled-Markets/dabl/tree/master/langref#processing-phases
	 */
	public CompilerState process() throws Exception {
		
		ClientState state = new ClientState();
		process(state);
		return state;
	}
	
	/**
	 * For use by all process methods. This method provides the common functionality.
	 * This version of process can also be used by tests that need to provide their
	 * own factory for creating ImportHandlers.
	 */
	public NameScope process(AnalyzerFactory analyzerFactory) throws Exception {
		
		// ....Need to insert template processor here
		// ....Alert if any template symbols have no value.
		
		NamespaceProcessor namespaceProcessor = analyzerFactory.createNamespaceProcessor();
		Reader r = new StringReader(DablStandard.PackageText);
		namespaceProcessor.processNamespace(r);
		namespaceProcessor.setPrettyPrint(this.print);
		NameScope nameScope = namespaceProcessor.processPrimaryNamespace(this.reader);
		
		return nameScope;
	}
	
	/**
	 * This version of process should only be called by ImportHandlers.
	 */
	public NameScope process(CompilerState state) throws Exception {
		
		return process(new DablAnalyzerFactory(state));
	}
}
