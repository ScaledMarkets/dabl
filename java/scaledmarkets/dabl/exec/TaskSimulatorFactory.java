package scaledmarkets.dabl.exec;

public class TaskSimulatorFactory {
	
	/**
	 * Create a pretend-container.
	 */
	public TaskContainer createTaskContainer() {
		return new PretendTaskContainer();
	}
	
	public boolean isASimulation() { return true; }
}
