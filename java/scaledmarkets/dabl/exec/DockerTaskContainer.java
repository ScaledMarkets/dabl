package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class DockerTaskContainer extends TaskContainer {
	
	private TaskContainerFactory taskContainerFactory;
	private DockerContainer dockerContainer;
	
	public DockerTaskContainer(TaskContainerFactory factory, DockerContainer dockerContainer) {
		
		this.taskContainerFactory = factory;
		this.dockerContainer = dockerContainer;
	}
	
	/**
	 * Perform a task's procedural statements.
	 * The container is contacted and instructed to perform the task.
	 */
	public void executeTask(Task task) throws Exception {
		
		// Start the container.
		this.dockerContainer.start();
		
		// Execute the task in the container.
		//....
		
		// Terminate the container.
		this.dockerContainer.destroy();
		this.taskContainerFactory.containerWasDestroyed(this);
	}
}
