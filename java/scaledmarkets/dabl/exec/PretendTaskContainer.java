package scaledmarkets.dabl.exec;

/**
 * Used by TaskSimulatorFactory for simulating task execution.
 */
public class PretendTaskContainer extends TaskContainer {
	
	private Task task;
	
	public PretendTaskContainer(Task task) {
		this.task = task;
	}
	
	public void execute() throws Exception {
		System.out.println(">> Simulator: Task " + this.task.getName() + " would be executed");
	}
}
