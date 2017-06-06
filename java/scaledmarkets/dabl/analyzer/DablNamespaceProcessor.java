package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public class DablNamespaceProcessor implements NamespaceProcessor {
	
	private AnalyzerFactory analyzerFactory;
	
	public DablNamespaceProcessor(AnalyzerFactory analyzerFactory) {
		this.analyzerFactory = analyzerFactory;
	}
	
	public NameScope processNamespace(Reader reader, CompilerState state) {

		assertThat(state instanceof ClientState,
			"CompilerState passed to Dabl namespace processor is not a ClientState");
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		
		// Analyze the AST.
		state.getASTs().add(start);
		Analyzer analyzer = this.analyzerFactory.createAnalyzer();
		start.apply(analyzer);

		return analyzer.getNamespaceNamescope();
	}
}
