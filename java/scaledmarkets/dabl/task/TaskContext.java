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
	
	private Map<String, Object> variables = new HashMap<String, Object>();
	
	public TaskContext(AOprogram program) {
		this.program = program;
	}
	
	public .....AOprogram getProgram() {
		return (AOprogram)(start.getPOprogram());.....
	}
	
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
	
	public void setVariable(String name, Object value) {
		variables.put(name, value);
	}
	
	public Object getVariable(String name) {
		return variables.get(name);
	}
}
