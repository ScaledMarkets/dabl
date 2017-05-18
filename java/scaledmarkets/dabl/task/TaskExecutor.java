package scaledmarkets.dabl.task;

import scaledmarkets.dabl.task.lexer.*;
import scaledmarkets.dabl.task.node.*;
import scaledmarkets.dabl.task.parser.*;
import scaledmarkets.dabl.task.analyzer.*;
import scaledmarkets.dabl.exec.*;

import java.util.LinkedList;

/**
 * For executing a DABL task inside of a container.
 * When a container is started by a TaskContainerFactory, the container is
 * given a TaskExecutor to run. The arguments to the TaskExecutor tell the
 * TaskExecutor where to find the definition of the Task that the TaskExecutor
 * is to run.
 */
public class TaskExecutor implements Executor {
	
	private TaskContext context;
	
	public static void main(String[] args) {

		// Obtain the input file, containing only function declarations and
		// procedural statements.
		
		// Parse the procedural statements.
		
		
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(this.reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		System.out.println("Syntax is correct");

		AOprogram program = (AOprogram)(start.getPOprogram());
		
		TaskContext context = new TaskContext();
		context.getASTs().add(start);
		
		/*
		TaskProgramAnalyzer analyzer = new TaskProgramAnalyzer(context);
		start.apply(analyzer);
		
		state.setPrimaryNamespaceSymbolEntry(analyzer.getEnclosingScopeEntry());
		return analyzer.getNamespaceNamescope();
		*/
		
		Executor exec = new TaskExecutor(context);
		try {
			exec.execute();
		}
		catch (Exception ex) {
			// Set process status
		}
	}
	
	/**
	 * Visit each proc stmt and execute it.
	 */
	public void execute() throws Exception {
		
		for (POprocStmt p : taskContext.getProgram().getOprocStmt()) {
			
			if (p instanceof AFuncCallOprocStmt) {
			
			} else if (p instanceof AIfErrorOprocStmt) {
				
			} else throw new RuntimeException(
				"proc stmt is not a known type: " + p.getClass().getName());
		
		}
		
	}
}
