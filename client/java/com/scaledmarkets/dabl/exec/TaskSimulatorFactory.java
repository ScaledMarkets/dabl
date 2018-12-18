package com.scaledmarkets.dabl.exec;

import java.util.Set;
import java.util.Map;
import java.util.Properties;
import java.io.File;

/**
 * For creating task containers in simulation mode, whereby containers are not
 * actually created, and tasks are not actually executed.
 */
public class TaskSimulatorFactory extends TaskContainerFactory {
	
	/**
	 * Create a pretend-container.
	 */
	public TaskContainer createTaskContainer(Task task, File workspace,
		Properties containerProperties) throws Exception {
		return new PretendTaskContainer(task);
	}
	
	public void containerWasDestroyed(TaskContainer container) {
		
		//....
	}

	public Set<TaskContainer> getTaskContainers() {
		
		return null;
		//....
	}

	public boolean isASimulation() { return true; }
}
