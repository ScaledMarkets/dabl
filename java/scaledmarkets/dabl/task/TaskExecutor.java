package scaledmarkets.dabl.task;

import scaledmarkets.dabl.task.lexer.*;
import scaledmarkets.dabl.task.node.*;
import scaledmarkets.dabl.task.parser.*;
import scaledmarkets.dabl.task.analyzer.*;
import scaledmarkets.dabl.exec.*;

import java.util.LinkedList;
import java.io.Reader;
import java.io.InputStreamReader;

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

		// Obtain the program to run, containing only function declarations and
		// procedural statements (AST defined by task.sablecc).
		Reader reader = new InputStreamReader(System.in);
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		System.out.println("Syntax is correct");

		// Create an analysis context and analyze the AST.
		TaskContext context = new TaskContext();
		context.getASTs().add(start);
		TaskProgramAnalyzer analyzer = new TaskProgramAnalyzer(context);
		start.apply(analyzer);
		
		// Execute the actions defined by the analyzed AST.
		Executor exec = new TaskExecutor(context);
		try {
			exec.execute();
		}
		catch (Exception ex) {
			// Set process status and exit.
			ex.printStackTrace();
			Runtime.exit(1);
		}
	}
	
	TaskExecutor(TaskContext context) {
		this.context = context;
	}
	
	/**
	 * Visit each proc stmt and execute it.
	 */
	public void execute() throws Exception {
		
		for (POprocStmt p : taskContext.getProgram().getOprocStmt()) {
			
			if (p instanceof AFuncCallOprocStmt) {
				
				AFuncCallOprocStmt funcCall = (AFuncCallOprocStmt)p;
				// oid_ref oexpr* otarget_opt
				
				// 
				POidRef pid = funcCall.getOidRef();
				AOidRef idRef = (AOidRef)pid;
				DeclaredEntry entry = getHelper().getDeclaredEntryForIdRef(idRef);
				
				AOfunctionDeclaration funcDecl = ....
				
				TId funcDecl.getName();
				LinkedList<POtypeSpec> funcDecl.getOtypeSpec();
				POstringLiteral funcDecl.getNativeLanguage();
				POstringLiteral funcDecl.getNativeName();
				POtypeSpec funcDecl.getReturnType();
				
				String lang = ....getHelper().getStringLiteralValue(funcDecl.getNativeLanguage());
				
				
				LinkedList<POexpr> pexs = funcCall.getOexpr();
				
				
				POtargetOpt ptopt = funcCall.getOtargetOpt();
				
				
				
				
			} else if (p instanceof AIfErrorOprocStmt) {
				
				// Install an error handler.
				
				
			} else throw new RuntimeException(
				"proc stmt is not a known type: " + p.getClass().getName());
		}
		
	}
}
