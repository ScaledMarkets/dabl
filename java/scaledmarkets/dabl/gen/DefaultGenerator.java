package scaledmarkets.dabl.gen;

public class DefaultGenerator implements Generator {
	
	private CompilerState state;
	
	public DefaultGenerator(CompilerState state) {
		this.state = state;
	}
	
	public void generate() {
		TaskContext taskContext = ....
		DependencyGraph graph = DependencyGraph.genDependencySet(this.state, taskContext);
		graph.executeAll();
		
		
	}
}
