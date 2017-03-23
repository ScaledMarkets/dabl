package scaledmarkets.dabl.exec;

/**
 * Used by TaskSimulatorFactory for simulating task execution.
 */
public class PretendTaskContainer extends TaskContainer {
	
	public void executeTask(Task task) throws Exception {
		System.out.println("Task " + task.getName() + " would be executed");
	}
}
