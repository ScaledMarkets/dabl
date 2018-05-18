package scaledmarkets.dabl.analyzer;

/**
 * Defines a type that is able to create instances of the various components that
 * are needed to process DABL input. The instances returned by a given factory
 * are all designed to work with each other.
 */
public interface AnalyzerFactory {
	NamespaceProcessor createNamespaceProcessor();
	Analyzer createAnalyzer(CompilerState state);
	ImportHandler createImportHandler();
	CompilerState getCompilerState();
}
