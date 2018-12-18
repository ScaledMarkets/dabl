package com.scaledmarkets.dabl.docker;

import java.io.InputStream;

/**
 * A wrapper for a docker container. Enables one to start, stop, and remove
 * the container.
 */
public class DockerContainer {
	
	private Docker docker;
	private String containerId;
	private InputStream containerOutput;
	
	public DockerContainer(Docker docker, String containerId) {
		this.docker = docker;
		this.containerId = containerId;
	}
	
	public String getContainerId() { return this.containerId; }
	
	public InputStream start(InputStream input) throws Exception {
		
		if (input == null) {
			this.containerOutput = null;
			this.docker.startContainer(this.containerId);
		} else {
			this.containerOutput = this.docker.startContainer(this.containerId, input);
		}
		
		return this.containerOutput;
	}
	
	public void stop() throws Exception {
		
		this.docker.stopContainer(this.containerId);
		if (this.containerOutput != null) try {
			this.containerOutput.close();
		} catch (Exception ex) {
			System.err.println("Error flushing or closing container output stream:");
			ex.printStackTrace();
		}
	}
	
	public void destroy() throws Exception {
		
		this.docker.destroyContainer(this.containerId);
	}
	
	public InputStream connectTo(InputStream input) throws Exception {
		return this.docker.connectToContainer(this.containerId, input);
	}
	
	public boolean isRunning() throws Exception {
		
		return this.docker.containerIsRunning(this.containerId);
	}
	
	public boolean exited() throws Exception {
		return this.docker.containerExited(this.containerId);
	}
	
	public boolean exists() throws Exception {
		return this.docker.containerExists(this.containerId);
	}
	
	public int getExitStatus() throws Exception {
		return this.docker.getExitStatus(this.containerId);
	}
}
