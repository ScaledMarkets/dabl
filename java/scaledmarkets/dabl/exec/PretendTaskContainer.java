package scaledmarkets.dabl.exec;

import java.io.PrintStream;

/**
 * Used by TaskSimulatorFactory for simulating task execution.
 */
public class PretendTaskContainer extends TaskContainer {
	
	private Task task;
	
	public PretendTaskContainer(Task task) {
		this.task = task;
	}
	
	public void execute(PrintStream taskOutput, int timeout) throws Exception {
		taskOutput.println(
			">> Simulator: Task " + this.task.getName() + " would be executed");
	}
	
	public void destroy() throws Exception {
	}
}
