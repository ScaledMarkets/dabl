package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
	public TaskContainer createTaskContainer(Task task, List<File> inputs,
		List<File> outputs) throws Exception {
		
		// Copy the inputs to a temp directory.
		....
		
		// Map the input and output locations to a temp dir such that the container
		// sees the temp directory contents as existing in their orignal host locations.
		....
		
	
	
		// Create a container for performing a task. (Do not start the container.)
		DockerContainer dockerContainer = this.docker.createContainer(
			this.dockerImageName, task.getName(),
			hostPathsToMap, hostPathsToMap); 
		
		// Return an object that can be used to control the container.
		TaskContainer taskContainer;
		synchronized (this) {
			taskContainer = new DockerTaskContainer(this, task, dockerContainer);
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
