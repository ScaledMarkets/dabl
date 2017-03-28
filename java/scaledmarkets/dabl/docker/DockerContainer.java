package scaledmarkets.dabl.docker;

public class DockerContainer {
	
	private Docker docker;
	private string containerId;
	
	public DockerContainer(Docker docker, String containerId) {
		this.docker = docker;
		this.containerId = containerId;
	}
	
	public void start() throws Exception {
		
		this.docker.startContainer(this.containerId);
	}
	
	public void stop() throws Exception {
		
		this.docker.stopContainer(this.containerId);
	}
	
	public void destroy() throws Exception {
		
		this.docker.destroyContainer(this.containerId);
	}
}
