package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public class DablNamespaceProcessor implements NamespaceProcessor {
	
	private Analyzer analyzer;
	
	public DablNamespaceProcessor(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public NameScope processNamespace(Reader reader) {

		assertThat(analyzer.getState() instanceof ClientState,
			"CompilerState passed to Dabl namespace processor is not a ClientState");
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		
		// Analyze the AST.
		analyzer.getState().getASTs().add(start);
		start.apply(this.analyzer);

		return analyzer.getNamespaceNamescope();
	}
}
