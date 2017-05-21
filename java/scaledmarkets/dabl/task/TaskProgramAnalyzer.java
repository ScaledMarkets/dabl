package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.DablBaseAdapter;
import scaledmarkets.dabl.analyzer.NamespaceImporter;
import java.io.Reader;
import java.io.StringReader;

/**
 * Annotate function call arguments with their values.
 */
public class TaskProgramAnalyzer extends DablBaseAdapter {
	
	private TaskContext context;
	
	public TaskProgramAnalyzer(TaskContext context) {
		
		this.context = context;
		
		Reader reader = new StringReader(DablStandard.PackageText);
		NamespaceImporter.importNamespace(reader, state);
	}
}
