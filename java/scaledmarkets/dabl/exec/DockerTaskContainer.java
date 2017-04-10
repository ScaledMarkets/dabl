package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class DockerTaskContainer extends TaskContainer {
	
	private TaskContainerFactory taskContainerFactory;
	private Task task;
	private DockerContainer dockerContainer;
	
	public DockerTaskContainer(TaskContainerFactory factory, Task task,
		DockerContainer dockerContainer) {
		
		this.taskContainerFactory = factory;
		this.task = task;
		this.dockerContainer = dockerContainer;
	}
	
	/**
	 * Perform a task's procedural statements.
	 * The container is contacted and instructed to perform the task.
	 */
	public void execute() throws Exception {
		
		// Start the container.
		this.dockerContainer.start();
		
		// Execute the task in the container.
		....
		
		// Write the outputs from the temp directory to the output directories.
		....
		
		// Terminate the container.
		this.dockerContainer.destroy();
		this.taskContainerFactory.containerWasDestroyed(this);
	}
}
