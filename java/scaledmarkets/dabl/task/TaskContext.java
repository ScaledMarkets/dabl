package scaledmarkets.dabl.task;

import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the task. Provides a task with access to the runtime,
 * when the task is executed. This can be used to obtain environment variables, etc.,
 * when the task is actually performed. A TaskContext is only available in
 * the environment in which the task runs (its container).
 */
public class TaskContext extends ExpressionContext {
	
	public Object getValueForVariable(String variableName) {
		return get(variableName);
	}
	
	public int getTaskStatus(String taskName) throws Exception {
		throw new Exception(
			"status of tasks is not available in a task's individual runtime context");
	}
	
	public Date getDateOfMostRecentChange(String name) throws Exception {
		//....
		throw new RuntimeException("Not implemented yet");
	}
}
