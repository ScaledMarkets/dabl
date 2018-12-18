package com.scaledmarkets.dabl.task;

import com.scaledmarkets.dabl.node.AOidRef;
import com.scaledmarkets.dabl.node.AOtaskDeclaration;
import com.scaledmarkets.dabl.node.POexpr;
import com.scaledmarkets.dabl.analyzer.CompilerState;
import com.scaledmarkets.dabl.analyzer.ExpressionContext;
import com.scaledmarkets.dabl.analyzer.ExpressionEvaluator;
import com.scaledmarkets.dabl.util.Utilities;
import com.scaledmarkets.dabl.helper.Helper;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.List;

/**
 * The container-side execution context of the task. Provides a task with access to the runtime,
 * when the task is executed. This can be used to obtain environment variables, etc.,
 * when the task is actually performed. A TaskContext is only available in
 * the environment in which the task runs (its container).
 */
public class TaskContext extends CompilerState {
	
	private TaskExpressionContext exprContext = new TaskExpressionContext();
	private ExpressionEvaluator exprEvaluator = new ExpressionEvaluator(exprContext);
	private Map<String, Integer> taskStatus = new HashMap<String, Integer>();
		
	/**
	 * 
	 */
	public AOtaskDeclaration getTaskDeclaration() throws Exception {
		Helper helper = new Helper(this);
		List<AOtaskDeclaration> taskDecls = helper.getTaskDeclarations();
		Utilities.assertThat(taskDecls.size() == 1, "There must be one task: there are " + taskDecls.size());
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
	public Date getDateOfMostRecentChange(AOidRef name) throws Exception {
		return this.exprContext.getDateOfMostRecentChange(name);
	}
	
	/**
	 * Evaluate the specified expression in the context of the current task.
	 */
	public Object evaluateExpr(POexpr expr) {
		return this.exprEvaluator.evaluateExpr(expr);
	}

	class TaskExpressionContext extends ExpressionContext {
		
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
		
		public Date getDateOfMostRecentChange(AOidRef inputOrOutputName) throws Exception {
			throw new Exception("Change history of an input or output is not available from a task");
		}
	};
}
