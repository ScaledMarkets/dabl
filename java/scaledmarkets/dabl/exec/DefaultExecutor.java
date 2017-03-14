package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.analysis.CompilerState;

/**
 * Determine which tasks should be executed, and execute them.
 */
public class DefaultExecutor implements Executor {
	
	private CompilerState state;
	private TaskContainerFactory taskContainerFactory;
	
	public DefaultExecutor(CompilerState state, TaskContainerFactory taskContainerFactory) {
		this.state = state;
		this.taskContainerFactory = taskContainerFactory;
	}
	
	public void execute() throws Exception {
		System.out.println("Determining dependency graph...");
		DependencyGraph graph = DependencyGraph.genDependencySet(this.state,
			this.taskContainerFactory, false);

		System.out.println("Executing tasks...");
		graph.executeAll();
		
		System.out.println("...done executing tasks.");
	}
}
