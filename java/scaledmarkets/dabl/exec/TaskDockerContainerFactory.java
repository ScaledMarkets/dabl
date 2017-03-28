package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.util.Map;
import java.util.TreeMap;

public class TaskDockerContainerFactory extends TaskContainerFactory {
	
	private Map<TaskContainer, TaskContainer> taskContainers = new TreeMap<TaskContainer, TaskContainer>();
	private Docker docker;
	
	public TaskDockerContainerFactory() {
		
		// Conntect to docker.
		this.docker = Docker.connect(....);
	}
	
	/**
	 * Create a container, containing the task interpreter. Return an object
	 * that enables one to control the container.
	 */
	public TaskContainer createTaskContainer() throws Exception {
		
		// Create a container for performing a task. (Do not start the container.)
		DockerContainer dockerContainer = this.docker.createContainer(String imageName); 
		
		// Return an object that can be used to control the container.
		synchronized {
			TaskContainer taskContainer = new DockerTaskContainer(this, dockerContainer);
			taskContainers.put(taskContainer, taskContainer);
		}
		return taskContainer;
	}
	
	public void containerWasDestroyed(TaskContainer container) {
		synchronized {
			taskContainers.remove(container);
		}
	}
	
	public Set<TaskContainer> getTaskContainers() {
		
		....
	}

	public boolean isASimulation() { return false; }
}
