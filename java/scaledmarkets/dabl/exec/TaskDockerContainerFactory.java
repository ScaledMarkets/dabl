package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.io.File;

public class TaskDockerContainerFactory extends TaskContainerFactory {
	
	public static String dockerImageName = "centos7.2";
	private Map<TaskContainer, TaskContainer> taskContainers = new TreeMap<TaskContainer, TaskContainer>();
	private Docker docker;
	
	public TaskDockerContainerFactory() throws Exception {
		
		// Conntect to docker.
		this.docker = Docker.connect();
	}
	
	/**
	 * Create a container, containing the task interpreter. Return an object
	 * that enables one to control the container.
	 */
	public TaskContainer createTaskContainer(Task task, File workspace) throws Exception {
		
		// Create a container for performing a task. (Do not start the container.)
		// The container maps the temp directory.
		DockerContainer dockerContainer = this.docker.createContainer(
			this.dockerImageName, task.getName(), workspace, workspace); 
		
		// Return an object that can be used to control the container.
		TaskContainer taskContainer;
		synchronized (this) {
			taskContainer = new DockerTaskContainer(task, dockerContainer, workspace);
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
