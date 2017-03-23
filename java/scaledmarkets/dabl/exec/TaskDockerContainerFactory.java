package scaledmarkets.dabl.exec;

public class TaskDockerContainerFactory extends TaskContainerFactory {
	
	/**
	 * Create a container, containing the task interpreter. Return an object
	 * that enables one to control the container.
	 */
	public TaskContainer createTaskContainer() {
		return new DockerTaskContainer();
	}
	
	public boolean isASimulation() { return false; }
}
