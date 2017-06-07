package scaledmarkets.dabl.analyzer;

/**
 * For creating analysis components that analyze DABL input in a client context.
 */
public class DablAnalyzerFactory implements AnalyzerFactory {
	public NamespaceProcessor createNamespaceProcessor() {
		ClientState state = new ClientState();
		return new DablNamespaceProcessor(createAnalyzer(state));
	}
	
	public Analyzer createAnalyzer(CompilerState state) {
		return new LanguageAnalyzer(state, createImportHandler());
	}
	
	public ImportHandler createImportHandler() {
		return new FileImportHandler(this);
	}
}
