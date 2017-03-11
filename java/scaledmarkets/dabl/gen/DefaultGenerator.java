package scaledmarkets.dabl.gen;

public class DefaultGenerator implements Generator {
	
	private CompilerState state;
	private TaskContext taskContext;
	
	public DefaultGenerator(CompilerState state, TaskContext taskContext) {
		this.state = state;
		this.taskContext = taskContext;
	}
	
	public void generate() {
		DependencyGraph graph = DependencyGraph.genDependencySet(this.state, this.taskContext);
		graph.executeAll();
		
		....
	}
}
