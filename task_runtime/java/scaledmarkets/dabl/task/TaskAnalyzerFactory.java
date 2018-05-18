package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.AnalyzerFactory;
import scaledmarkets.dabl.analyzer.NamespaceProcessor;
import scaledmarkets.dabl.analyzer.Analyzer;
import scaledmarkets.dabl.analyzer.ImportHandler;
import scaledmarkets.dabl.analyzer.FileImportHandler;
import scaledmarkets.dabl.analyzer.CompilerState;
import scaledmarkets.dabl.util.Utilities;

/**
 * For creating analysis components that analyze DABL input in a task execution context.
 */
public class TaskAnalyzerFactory implements AnalyzerFactory {

	TaskAnalyzerFactory() {
		this.taskContext = new TaskContext();
	}
	
	public CompilerState getCompilerState() { return this.taskContext; }
	
	public TaskContext getTaskContext() { return this.taskContext; }
	
	public NamespaceProcessor createNamespaceProcessor() {
		//TaskContext taskContext = new TaskContext();
		return new NamespaceProcessor(createAnalyzer(taskContext));
	}
	
	public Analyzer createAnalyzer(CompilerState state) {
		Utilities.assertThat(state instanceof TaskContext,
			"Expected a TaskContext, but received a " + state.getClass().getName());
		return new TaskProgramAnalyzer((TaskContext)state, createImportHandler());
	}
	
	public ImportHandler createImportHandler() {
		return new FileImportHandler(this);
	}
	
	private TaskContext taskContext;
}
