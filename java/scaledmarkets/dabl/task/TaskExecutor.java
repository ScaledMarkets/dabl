package scaledmarkets.dabl.task;

import scaledmarkets.dabl.Executor;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.analyzer.ValueType;
import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.util.Utilities;

import java.util.List;
import java.util.LinkedList;
import java.io.Reader;
import java.io.StringReader;
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
	private Helper helper;
	
	public static void main(String[] args) {

		java.io.File f = new java.io.File("output.txt");  // debug
		java.io.PrintWriter w;    // debug
		try {  // debug
			w = new java.io.PrintWriter(f);  // debug
		} catch (Exception ex) {  // debug
			ex.printStackTrace();  // debug
			return;  // debug
		}  // debug
		
		
		System.out.println("Beginning execution of task...");
		w.println("Beginning exec of task...");  // debug
		TaskAnalyzerFactory analyzerFactory = new TaskAnalyzerFactory();
		NamespaceProcessor namespaceProcessor = analyzerFactory.createNamespaceProcessor();

		// Process the Dabl standard package.
		String omitPackageStandardStr = System.getenv("OmitPackageStandard");
		if ((omitPackageStandardStr == null) || (! omitPackageStandardStr.equals("true"))) {
			Reader r = new StringReader(DablStandard.PackageText);
			namespaceProcessor.processNamespace(r);
			System.out.println("Processed DablStandard.");
			w.println("Processed DablStandard.");  // debug
		}
		
		// Obtain the program to run, containing only function declarations and
		// procedural statements (AST defined by task.sablecc).
		Reader reader = new InputStreamReader(System.in);
		
		// Parse and analyze the input.
		NameScope nameScope = namespaceProcessor.processPrimaryNamespace(reader);
		System.out.println("Parsed input.");
		w.println("Parsed input.");  // debug
		
		// Create a TaskExecutor, which will execute the actions defined by
		// the analyzed AST. If task execution produces an error, set the
		// process status accordingly.
		int status = -1;
		try {
			(new TaskExecutor(analyzerFactory.getTaskContext())).execute();
			status = 0;
		}
		catch (RuntimeException rex) {
			rex.printStackTrace();
			status = 2;
		}
		catch (Exception ex) {
			ex.printStackTrace();
			status = 1;
		}
		catch (Throwable t) {
			t.printStackTrace();
			status = 3;
		}
		finally {
			System.out.println("Executed task.");
			w.println("Executed task.");  // debug
			Runtime.getRuntime().exit(status);
		}
	}
	
	TaskExecutor(TaskContext context) {
		this.context = context;
		this.helper = new Helper(context);
	}
	
	/**
	 * Visit each proc stmt and execute it.
	 */
	public void execute() throws Exception {
		
		performProcStmts(this.context.getTaskDeclaration().getOprocStmt(), true);
	}

	/**
	 * Perform each of a list of procedural statements. Recursive, since a
	 * procedural statment may invoke a error handler - which contains 
	 * procedural statements. If 'recover' is true, then an error results
	 * in the invocation of the current error handler; otherwise, the error
	 * is thrown.
	 */
	protected void performProcStmts(List<POprocStmt> procStmts, boolean recover) throws Exception {
		
		for (POprocStmt p : procStmts) {
			
			if (p instanceof AFuncCallOprocStmt) {
				
				AFuncCallOprocStmt funcCall = (AFuncCallOprocStmt)p;
				// oid_ref oexpr* otarget_opt
				
				// Locate the function's declaration.
				POidRef pid = funcCall.getOidRef();
				AOidRef idRef = (AOidRef)pid;
				DeclaredEntry entry = getHelper().getDeclaredEntryForIdRef(idRef);
				Utilities.assertThat(entry != null, "No entry found for function " + idRef.toString());
				Node n = entry.getDefiningNode();
				Utilities.assertThat(n instanceof AOfunctionDeclaration,
					idRef.toString() + " was expected to be a function declaration");
				AOfunctionDeclaration funcDecl = (AOfunctionDeclaration)n;
				
				// Determine the function language and native name.
				String lang = getHelper().getStringLiteralValue(funcDecl.getTargetLanguage());
				String funcNativeName = getHelper().getStringLiteralValue(funcDecl.getTargetName());
				
				// Allocate a variable into which to place the function return result, if any.
				POtargetOpt ptopt = funcCall.getOtargetOpt();
				Object[] targetVariableRef = null;
				if (ptopt instanceof ATargetOtargetOpt) { // there is a target
					// Create target variable.
					targetVariableRef = new Object[1];
				}
				
				// Obtain the function call actual argument expressions, and evaluate
				// each one.
				List<Object> argValues = new LinkedList<Object>();
				for (POexpr expr : funcCall.getOexpr()) {
					argValues.add(this.context.evaluateExpr(expr));
				}
				
				// Determine the function declared argument types and actual argument types.
				List<ValueType> declaredArgTypes = getHelper().getFunctionDeclTypes(funcDecl);
				List<ValueType> argValueTypes = getHelper().getFunctionCallTypes(funcCall);
				
				// Check that the actual argument types are compatible with the declared types.
				ValueType.checkTypeListAssignabilityTo(argValueTypes, declaredArgTypes);
				
				// Obtain a handler that can be used to perform the function call
				// in the function's language.
				FunctionHandler handler = getFunctionHandler(lang);
				
				// Call the function.
				try {
					handler.callFunction(funcNativeName,
						argValues.toArray(new Object[argValues.size()]), targetVariableRef);
					
					if (targetVariableRef != null) { // there is a target
						// Check that the value that was returned conforms to the
						// declared return type.
						Object returnValue = targetVariableRef[0];
						List<POtypeSpec> ptps = funcDecl.getReturnType();
						if (ptps.size() == 0) {
							Utilities.assertThat(false,
								"No return value found for function " + funcNativeName);
						} else if (ptps.size() == 1) {
							ValueType returnType = LanguageCoreAnalyzer.mapTypeSpecToValueType(ptps.get(0));
							returnType.checkNativeTypeAssignabilityFrom(returnType.getClass());
							
							// Transfer return value to target.
							this.context.setValueForVariable(ptopt.toString(), returnValue);
						} else {
							Utilities.assertThat(false,
								"More than one return value for function " + funcNativeName);
						}
					}
				} catch (Throwable t) {
					
					if (errorHandler == null) throw t;
					if (! recover) throw t;
					
					errorHandler.invoke();
				}
				
			} else if (p instanceof AIfErrorOprocStmt) {
				
				AIfErrorOprocStmt ifErrorStmt = (AIfErrorOprocStmt)p;
				
				// Install an error handler.
				this.errorHandler = new ErrorHandler() {
					
					public void invoke() throws Exception {
						performProcStmts(ifErrorStmt.getOprocStmt(), false);
					}
				};
				
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
	
	protected Helper getHelper() { return this.helper; }
}
