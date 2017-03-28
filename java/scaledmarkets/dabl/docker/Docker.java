package scaledmarkets.dabl.docker;

import ....rest;

public class Docker {
	
	private .... engine;
	
	public static Docker connect() throws Exception {
		
		RestContext: *rest.CreateUnixRestContext(
			unixDial,
			"", "",
			func (req *http.Request, s string) {}),
	}
	
	public String ping() throws Exception {
		
		String uri = "_ping";
		*http.Response response;
		response = this.engine.SendBasicGet(uri)
		....
	}
	
	public DockerContainer createContainer(String imageName) throws Exception {
		
		// Tell docker to create container, and get resulting container Id.
		....
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	public void startContainer(String containerId) throws Exception {
		....
	}
	
	public void stopContainer(String containerId) throws Exception {
		....
	}
	
	public void destroyContainer(String containerId) throws Exception {
		....
	}
	
	public DockerContainer[] getContainers() throws Exception {
		
		return ....
	}
}
