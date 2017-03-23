package scaledmarkets.dabl.exec;

public class TaskSimulatorFactory extends TaskContainerFactory {
	
	/**
	 * Create a pretend-container.
	 */
	public TaskContainer createTaskContainer() {
		return new PretendTaskContainer();
	}
	
	public boolean isASimulation() { return true; }
}
