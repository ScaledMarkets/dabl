package scaledmarkets.dabl.exec;

import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the Dabl process.
 */
public class DablContext extends ExpressionContext {
	
	public Object getValueForVariable(String variableName) {
		throw new RuntimeException("Variables are only available in a task's runtime context");
	}
	
	public int getTaskStatus(String taskName) throws Exception {
		Object obj = get(taskName);
		if (obj == null) throw new Exception("Task has not executed");
		if (!( obj instanceof Integer)) throw new RuntimeException(
			"Status of task " + taskName + " is not an integer");
		return ((Integer)obj).intValue();
	}
	
	/**
	 * Return the most recent date/time at which the input or output represented
	 * by the specified identifier was changed.
	 */
	public Date getDateOfMostRecentChange(String name) throws Exception {
		//....
		throw new RuntimeException("Not implemented yet");
	}
}
