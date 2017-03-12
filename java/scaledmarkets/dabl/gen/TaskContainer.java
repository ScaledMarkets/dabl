package scaledmarkets.dabl.gen;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class TaskContainer {
	
	/**
	 * Perform a task's procedural statements.
	 * The container is contacted and instructed to perform the task.
	 */
	public void executeTask(Task task) throws Exception {
		//....
		System.out.println("executeTask " + task.getName());
	}
}
