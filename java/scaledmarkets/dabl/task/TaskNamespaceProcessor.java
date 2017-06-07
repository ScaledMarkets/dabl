package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public class TaskNamespaceProcessor implements NamespaceProcessor {
	
	private Analyzer analyzer;
	
	public TaskNamespaceProcessor(Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	public NameScope processNamespace(Reader reader) {

		assertThat(analyzer.getState() instanceof TaskContext,
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
