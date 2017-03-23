package scaledmarkets.dabl.exec;

/**
 * For creating task containers in simulation mode, whereby containers are not
 * actually created, and tasks are not actually executed.
 */
public class TaskSimulatorFactory extends TaskContainerFactory {
	
	/**
	 * Create a pretend-container.
	 */
	public TaskContainer createTaskContainer() {
		return new PretendTaskContainer();
	}
	
	public boolean isASimulation() { return true; }
}
