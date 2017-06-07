package scaledmarkets.dabl.analyzer;

public class DablAnalyzerFactory {
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
