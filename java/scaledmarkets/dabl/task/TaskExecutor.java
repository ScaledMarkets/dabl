package scaledmarkets.dabl.task;

/**
 * For executing a DABL task inside of a container.
 * When a container is started by a TaskContainerFactory, the container is
 * given a TaskExecutor to run. The arguments to the TaskExecutor tell the
 * TaskExecutor where to find the definition of the Task that the TaskExecutor
 * is to run.
 */
public class TaskExecutor {
	
	public static void main(String[] args) {

		// Obtain the input file, containing only function declarations and
		// procedural statements.
		
		// Parse the procedural statements.
		
		
		TaskContext taskContext = new TaskContext();
		
		// Parse the input and generate an AST.
		/*
		Lexer lexer = new Lexer(new PushbackReader(this.reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		System.out.println("Syntax is correct");
		state.getASTs().add(start);
		
		TaskProgramAnalyzer analyzer = new TaskProgramAnalyzer(state, this.importHandler);
		start.apply(analyzer);
		
		state.setPrimaryNamespaceSymbolEntry(analyzer.getEnclosingScopeEntry());
		return analyzer.getNamespaceNamescope();
		
		Executor exec = new TaskExecutor(state, taskContainerFactory, simulate);
		try {
			exec.execute();
		}
		catch (Exception ex) {
		}
		*/
	}
	
}
