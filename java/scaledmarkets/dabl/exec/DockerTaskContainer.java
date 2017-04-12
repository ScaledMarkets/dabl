package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.io.File;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class DockerTaskContainer extends TaskContainer {
	
	private Task task;
	private DockerContainer dockerContainer;
	private File workspace;
	
	public DockerTaskContainer(Task task, DockerContainer dockerContainer, File workspace) {
		
		this.task = task;
		this.dockerContainer = dockerContainer;
		this.workspace = workspace;
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
		
		
	}
}
