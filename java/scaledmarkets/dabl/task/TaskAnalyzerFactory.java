package scaledmarkets.dabl.task;

public class TaskAnalyzerFactory {
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
