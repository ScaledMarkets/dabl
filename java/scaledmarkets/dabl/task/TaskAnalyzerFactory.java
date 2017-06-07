package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.AnalyzerFactory;
import scaledmarkets.dabl.analyzer.NamespaceProcessor;
import scaledmarkets.dabl.analyzer.Analyzer;
import scaledmarkets.dabl.analyzer.ImportHandler;
import scaledmarkets.dabl.analyzer.FileImportHandler;
import scaledmarkets.dabl.analyzer.CompilerState;

/**
 * For creating analysis components that analyze DABL input in a task execution context.
 */
public class TaskAnalyzerFactory implements AnalyzerFactory {
	public NamespaceProcessor createNamespaceProcessor() {
		return new TaskNamespaceProcessor(createAnalyzer());
	}
	
	public Analyzer createAnalyzer(CompilerState state) {
		return new TaskProgramAnalyzer(state, createImportHandler());
	}
	
	public ImportHandler createImportHandler() {
		return new FileImportHandler(this);
	}
}
