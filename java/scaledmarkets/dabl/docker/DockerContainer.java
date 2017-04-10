package scaledmarkets.dabl.docker;

/**
 * A wrapper for a docker container. Enables one to start, stop, and remove
 * the container.
 */
public class DockerContainer {
	
	private Docker docker;
	private String containerId;
	
	public DockerContainer(Docker docker, String containerId) {
		this.docker = docker;
		this.containerId = containerId;
	}
	
	public String getContainerId() { return this.containerId; }
	
	public void start() throws Exception {
		
		this.docker.startContainer(this.containerId);
	}
	
	public void stop() throws Exception {
		
		this.docker.stopContainer(this.containerId);
	}
	
	public void destroy() throws Exception {
		
		this.docker.destroyContainer(this.containerId);
	}
	
	public boolean isRunning() throws Exception {
		
		return this.docker.containerIsRunning(this.containerId);
	}
	
	public boolean exists() throws Exception {
		return this.docker.containerExists(this.containerId);
	}
}
