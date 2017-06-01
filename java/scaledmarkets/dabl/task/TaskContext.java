package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.CompilerState;
import scaledmarkets.dabl.exec.ExpressionContext;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the task. Provides a task with access to the runtime,
 * when the task is executed. This can be used to obtain environment variables, etc.,
 * when the task is actually performed. A TaskContext is only available in
 * the environment in which the task runs (its container).
 */
public class TaskContext extends CompilerState {
	
	private ExpressionContext exprContext = new ExpressionContext() {
		
		private Map<String, Object> variableValues = new HashMap<String, Object>();
		
		public Object getValueForVariable(String variableName) {
			return this.variableValues.get(variableName);
		}
		
		void setValueForVariable(String name, Object value) {
			this.variableValues.put(name, value);
		}
		
		public int getTaskStatus(String taskName) throws Exception {
			throw new Exception("Status of other tasks is not available from a task");
		}
		
		public Date getDateOfMostRecentChange(String inputOrOutputName) throws Exception {
			....
		}
	}
	
	private ExpressionEvaluator exprEvaluator = new ExpressionEvaluator(exprContext);
		
	/**
	 * 
	 */
	public AOtaskDeclaration getTaskDeclaration() throws Exception {
		List<AOtaskDeclaration> taskDecls = getHelper().getTaskDeclarations();
		assertThat(taskDecls.size() == 1, "There must be one task: there are " + taskDecls.size());
		return taskDecls.get(0);
	}
	
	/**
	 * 
	 */
	public Object getValueForVariable(String variableName) {
		return this.exprContext.getValueForVariable(variableName);
	}
	
	/**
	 * 
	 */
	public void setValueForVariable(String name, Object value) {
		this.exprContext.setValueForVariable(name, value);
	}
	
	/**
	 * 
	 */
	public int getTaskStatus(String taskName) throws Exception {
		return this.exprContext.getTaskStatus(taskName);
	}
	
	/**
	 * 
	 */
	public void setTaskStatus(String taskName, int status) throws Exception {
		return this.exprContext.putTaskStatus(taskName, status);
	}
	
	/**
	 * 
	 */
	public Date getDateOfMostRecentChange(String name) throws Exception {
		return this.exprContext.getDateOfMostRecentChange(name);
	}
	
	/**
	 * Evaluate the specified expression in the context of the current task.
	 */
	public Object evaluateExpr(POexpr expr) {
		return this.exprEvaluator.evaluateExpr(expr);
	}
}
