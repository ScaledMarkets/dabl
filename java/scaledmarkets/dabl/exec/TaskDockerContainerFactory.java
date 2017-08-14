package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import scaledmarkets.dabl.util.Utilities;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.HashMap;
import java.io.File;

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
		
		// Create a container for performing a task. (Do not start the container.)
		// The container maps the temp directory.
		DockerContainer dockerContainer = this.docker.createContainer2(
			dockerImageName, task.getName(), workspace.getCanonicalPath(),
			workspace.getCanonicalPath(), task.isOpen(), containerProperties); 
		
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

	public boolean isASimulation() { return false; }
}
