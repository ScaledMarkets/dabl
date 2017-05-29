package scaledmarkets.dabl.task;

import scaledmarkets.dabl.task.lexer.*;
import scaledmarkets.dabl.task.node.*;
import scaledmarkets.dabl.task.parser.*;
import scaledmarkets.dabl.task.analyzer.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.analyzer.ValueType;

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
	private ErrorHandler errorHandler;
	
	public static void main(String[] args) {

		// Obtain the program to run, containing only function declarations and
		// procedural statements (AST defined by task.sablecc).
		Reader reader = new InputStreamReader(System.in);
		
		// Parse the input and generate an AST.
		Lexer lexer = new Lexer(new PushbackReader(reader));
		Parser parser = new Parser(lexer);
		Start start = parser.parse();
		System.out.println("Syntax is correct");
		
		// Create a import handler that will analyze according to the rules of
		// the TaskProgramAnalyzer.
		ImportHandler importHandler = ....

		// Create an analysis context and analyze the AST.
		TaskContext context = new TaskContext();
		context.getASTs().add(start);
		TaskProgramAnalyzer analyzer = new TaskProgramAnalyzer(context, importHandler);
		start.apply(analyzer);
		
		// Create a TaskExecutor, which will execute the actions defined by
		// the analyzed AST. If task execution produces an error, set the
		// process status accordingly.
		int status;
		try {
			(new TaskExecutor(context)).execute();
			status = 0;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			status = 1;
		}
		catch (RuntimeException rex) {
			rex.printStackTrace();
			status = 2;
		}
		catch (Throwable t) {
			t.printStackTrace();
			status = 3;
		}
		finally {
			Runtime.exit(status);
		}
	}
	
	TaskExecutor(TaskContext context) {
		this.context = context;
	}
	
	/**
	 * Visit each proc stmt and execute it.
	 */
	public void execute() throws Throwable {
		
		performProcStmts(taskContext.getProgram().getOprocStmt(), true);
	}
	
	/**
	 * Perform each of a list of procedural statements. Recursive, since a
	 * procedural statment may invoke a error handler - which contains 
	 * procedural statements. If 'recover' is true, then an error results
	 * in the invocation of the current error handler; otherwise, the error
	 * is thrown.
	 */
	protected void performProcStmts(List<POprocStmt> procStmts, boolean recover) throws Throwable {
		
		for (POprocStmt p : procStmts) {
			
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
				List<Object> argValues = new LinkedList<Object>();
				
				
				
				
				POtargetOpt ptopt = funcCall.getOtargetOpt();
				if there is a target {
					Create target variable, of the specified type
				}
				
				ValueType.checkTypeListConformance(....declaredArgTypes, ....argValueTypes);
				
				FunctionHandler handler = getFunctionHandler(lang);
				
				try {
					handler.callFunction(funcNativeName, declaredArgTypes, argValues, 
						declaredTargetType, targetVariableRef);
				} catch (Throwable t) {
					
					if (errorHandler == null) throw t;
					if (! recover) throw t;
					
					errorHandler.invoke(t);
				}
				
			} else if (p instanceof AIfErrorOprocStmt) {
				
				AIfErrorOprocStmt ifErrorStmt = (AIfErrorOprocStmt)p;
				
				// Install an error handler.
				this.errorHandler = new ErrorHandler() {
					
					public void invoke() throws Throwable {
						performProcStmts(ifErrorStmt.getOprocStmt(), false);
					}
				}
				
			} else throw new RuntimeException(
				"proc stmt is not a known type: " + p.getClass().getName());
		}
	}
	
	/**
	 * Find and load a function handler for the specified language.
	 */
	protected FunctionHandler getFunctionHandler(String lang) throws Exception {
		
		String handlerClassName = Utilities.getSetting("dabl.function_handler." + lang);
		if (handlerClassName == null) throw new Exception(
			"Unrecognized function language " + lang);
		
		// Load the handler class.
		Class handlerClass = Class.forName(handlerClassName);
		
		// Create an instance of the handler class.
		Object obj = handlerClass.newInstance();
		if (! (obj instanceof FunctionHandler)) throw new Error(
			"Class " + handlerClass.getName() + " is not a FunctionHandler");
		
		return (FunctionHandler)obj;
	}
}
