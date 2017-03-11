package scaledmarkets.dabl.gen;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class TaskContainer {

	
	/**
	 * Perform a task's procedural statements. This should be done in isolation.
	 * Therefore, this is performed in a container.
	 * Provide the task procedural statements as a file parameter.
	 */
	public void executeTask(Task task) {
		....
	}
}
