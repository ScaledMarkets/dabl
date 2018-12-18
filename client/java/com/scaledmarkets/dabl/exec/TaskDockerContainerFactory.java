package com.scaledmarkets.dabl.exec;

import com.scaledmarkets.dabl.docker.*;
import com.scaledmarkets.dabl.util.Utilities;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class TaskDockerContainerFactory extends TaskContainerFactory {
	
	private Map<TaskContainer, TaskContainer> taskContainers = new HashMap<TaskContainer, TaskContainer>();
	private Docker docker;
	
	public TaskDockerContainerFactory() throws Exception {
		
		// Conntect to docker.
		this.docker = Docker.connect();
	}
	
	/**
	 * Create a container, containing the task interpreter. Return an object
	 * that enables one to control the container.
	 */
	public TaskContainer createTaskContainer(Task task, File workspace,
		Properties containerProperties) throws Exception {
		
		String dockerImageName = Utilities.getSetting("dabl.task_container_image_name");
		if ((dockerImageName == null) || dockerImageName.equals("")) throw new Exception(
			"Unable to identify container image to use");
		
		// Create a name for the container that will be unique in the container's
		// environment.
		String containerUniqueName = createUniqueContainerName(task.getName());
		
		// Create a container for performing a task. (Do not start the container.)
		// The container maps the temp directory.
		DockerContainer dockerContainer = this.docker.createContainer2(
			dockerImageName, containerUniqueName, workspace.getCanonicalPath(),
			workspace.getCanonicalPath(), task.isOpen(), true, containerProperties); 
		
		// Return an object that can be used to control the container.
		TaskContainer taskContainer;
		synchronized (this) {
			taskContainer = new DockerTaskContainer(task, dockerContainer, workspace);
			taskContainer.validateRequiredConfiguration();
			taskContainers.put(taskContainer, taskContainer);
		}
		return taskContainer;
	}
	
	public void containerWasDestroyed(TaskContainer container) {
		synchronized (this) {
			taskContainers.remove(container);
		}
	}
	
	public Set<TaskContainer> getTaskContainers() {
		return taskContainers.keySet();
	}
	
	/**
	 * Create a container name that is unique in the container's environment.
	 */
	public String createUniqueContainerName(String baseName) throws IOException {
		File file = File.createTempFile(baseName, "");
		return file.getName();
	}

	public boolean isASimulation() { return false; }
}
