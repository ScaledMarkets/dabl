package scaledmarkets.dabl.exec;

import java.io.InputStream;

/**
 * Provides ability to control a container, in which a TaskExecutor is running.
 */
public abstract class TaskContainer {
	
	/**
	 * Perform a task's procedural statements.
	 * The container is contacted and instructed to perform the task.
	 * Return the stdout and stderr of the container.
	 */
	public abstract InputStream execute() throws Exception;
	
	//public abstract int waitForContainerToExit(long maxMilliseconds) throws Exception;
	
	public abstract int getExitStatus() throws Exception;
	
	public abstract void stop() throws Exception;
	
	public abstract void destroy() throws Exception;
	
	/**
	 * Verify that the container has the configuration that is required in order
	 * to execute DABL tasks.
	 */
	public abstract void validateRequiredConfiguration() throws Exception;
}
