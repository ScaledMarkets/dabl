package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.docker.*;
import java.io.File;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public class DockerTaskContainer extends TaskContainer {
	
	private Task task;
	private DockerContainer dockerContainer;
	private File workspace;
	private boolean omitPackageStandard;
	
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
	public InputStream execute() throws Exception {
		
		// See https://docs.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html
		InputStream inputToContainer = new ByteArrayInputStream(
			task.getTaskProgram().getBytes(Charset.forName("ISO-8859-1")));
		
		System.out.println("About to execute task program...");
		
		// Start the container.
		// The container starts with its configured entrypoint, which is a
		// call to the TaskExecutor JAR.
		// Each procedural statement is passed via stdin, using the current thread.
		// The call below will block until the container has read all of the commands.
		return this.dockerContainer.start(inputToContainer);
	}
	
	/*
	public int waitForContainerToExit(long maxMilliseconds) throws Exception {
		
		long ms = 0;
		for (;;) {
			try {
				System.out.println("Calling getExitStatus...");  // debug
				return getExitStatus();
			} catch (Exception ex) {
				Thread.currentThread().sleep(1000);
				ms += 1000;
				if (ms > maxMilliseconds) throw ex;
			}
		}
	}
	*/
	
	public int getExitStatus() throws Exception {
		return dockerContainer.getExitStatus();
	}
	
	public void stop() throws Exception {
		this.dockerContainer.stop();
	}

	public void destroy() throws Exception {
		
		dockerContainer.destroy();
	}
	
	public void validateRequiredConfiguration() throws Exception {
		// Verify that the required base image is present.
		//....
		
		
	}
}
