package scaledmarkets.dabl.exec;

import java.util.Set;

public abstract class TaskContainerFactory {
	
	/**
	 * Create a container, containing the task interpreter. Return an object
	 * that enables one to control the container.
	 */
	public abstract TaskContainer createTaskContainer() throws Exception;
	
	/**
	 * Callback: notify this factory that the underlying container was destroyed,
	 * so that this factory can remove it from its list of containers.
	 */
	public abstract void containerWasDestroyed(TaskContainer container);
	
	public abstract Set<TaskContainer> getTaskContainers();

	public abstract boolean isASimulation();
}
