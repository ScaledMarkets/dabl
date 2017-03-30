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
	
	public void start(String[] pathsToMap) throws Exception {
		
		this.docker.startContainer(this.containerId, pathsToMap);
	}
	
	public void stop() throws Exception {
		
		this.docker.stopContainer(this.containerId);
	}
	
	public void destroy() throws Exception {
		
		this.docker.destroyContainer(this.containerId);
	}
}
