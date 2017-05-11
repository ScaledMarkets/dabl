package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.io.File;
import java.io.PrintStream;

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
	 * Direct container stdout to the specified print stream.
	 */
	public void execute(PrintStream taskOutput, int timeout) throws Exception {
		
		// Start the container.
		// The container starts with its configured entrypoint, which is a
		// call to the TaskExecutor JAR. Each procedural statement is passed
		// via stdin.
		Write stmtWriter = new ....
		Reader reader = this.dockerContainer.start(stmtWriter);
		....where should we send the container stdout?
		
		int exitStatus = this.dockerContainer.getExitStatus();
		if (exitStatus != 0) throw new Exception(
			"Container had exit status " + exitStatus);
	}
	
	public void destroy() throws Exception {
		
		dockerContainer.destroy();
	}
}
