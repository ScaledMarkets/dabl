package scaledmarkets.dabl.exec;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the Dabl process.
 */
public class DablContext extends ExpressionContext {
	
	private Map<String, int> taskStatus = new HashMap<String, int>();
	
	public Object getValueForVariable(String variableName) {
		throw new RuntimeException("Variables are only available in a task's runtime context");
	}
	
	public int getTaskStatus(String taskName) throws Exception {
		Object obj = this.taskStatus.get(taskName);
		if (obj == null) throw new Exception("Task has not executed");
		if (!( obj instanceof Integer)) throw new RuntimeException(
			"Status of task " + taskName + " is not an integer");
		return ((Integer)obj).intValue();
	}
	
	void setTaskStatus(String taskName, int status) {
		this.taskStatus.put(taskName, status);
	}
	
	/**
	 * Return the most recent date/time at which the input or output represented
	 * by the specified identifier was changed.
	 */
	public Date getDateOfMostRecentChange(String name) throws Exception {
		....
	}
}
