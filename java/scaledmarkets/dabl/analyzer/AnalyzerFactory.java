package scaledmarkets.dabl.analyzer;

public interface AnalyzerFactory {
	NamespaceProcessor createNamespaceProcessor();
	Analyzer createAnalyzer(CompilerState state);
	ImportHandler createImportHandler();
}
