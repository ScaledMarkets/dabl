package scaledmarkets.dabl.gen;

import scaledmarkets.dabl.analysis.CompilerState;

/**
 * Determine which tasks should be executed, and execute them.
 */
public class DefaultGenerator implements Generator {
	
	private CompilerState state;
	private TaskContainerFactory taskContainerFactory;
	
	public DefaultGenerator(CompilerState state, TaskContainerFactory taskContainerFactory) {
		this.state = state;
		this.taskContainerFactory = taskContainerFactory;
	}
	
	public void generate() {
		DependencyGraph graph = DependencyGraph.genDependencySet(this.state, this.taskContainerFactory);
		graph.executeAll();
	}
}
