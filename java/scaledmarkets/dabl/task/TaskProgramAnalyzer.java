package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.DablBaseAdapter;

public class TaskProgramAnalyzer extends DablBaseAdapter {
	
	private TaskContext context;
	
	public TaskProgramAnalyzer(TaskContext context) {
		
		this.context = context;
	}
}
