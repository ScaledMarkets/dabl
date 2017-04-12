package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.io.File;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class DockerTaskContainer extends TaskContainer {
	
	private TaskContainerFactory taskContainerFactory;
	private Task task;
	private DockerContainer dockerContainer;
	private File workspace;
	
	public DockerTaskContainer(TaskContainerFactory factory, Task task,
		DockerContainer dockerContainer, File workspace) {
		
		this.taskContainerFactory = factory;
		this.task = task;
		this.dockerContainer = dockerContainer;
		this.workspace = workspace;
	}
	
	/**
	 * Perform a task's procedural statements.
	 * The container is contacted and instructed to perform the task.
	 */
	public void execute() throws Exception {
		
		// Obtain inputs and copy them into the workspace.
		....
		
		
		// Start the container.
		this.dockerContainer.start();
		
		// Execute the task in the container.
		....
		
		// Write the outputs from the workspace to the output directories.
		....
		
		// Terminate the container.
		this.dockerContainer.destroy();
		this.taskContainerFactory.containerWasDestroyed(this);
	}
}
