package scaledmarkets.dabl.docker;

import javax.ws.rs.core.*;
import javax.ws.rs.client.*;

public class Docker {
	
	private static const String DockerURL = "unix:///var/run/docker.sock";
	private WebTarget endpoint;
	
	public static Docker connect() throws Exception {
		
		Client client = ClientBuilder.newClient();
		this.endpoint = client.target(DockerURL);
		
		
		// Verify that the required base image is present.
		....
	}
	
	/**
	 * Verify that the docker daemon is running, and verify that the required
	 * base image is available.
	 */
	public String ping() throws Exception {
		
		WebTarget target = this.endpoint.path("_ping");
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
				invocationBuilder.header("some-header", "true");

		Response response = invocationBuilder.get();

		System.out.println(response.getStatus());
	}
	
	public DockerContainer createContainer(String imageName) throws Exception {
		
		// Tell docker to create container, and get resulting container Id.
		WebTarget target = this.endpoint.path("_ping");
		
		WebTarget targetWithQueryParam =
			target.queryParam("greeting", "Hi World!");
		
		Invocation.Builder invocationBuilder =
			targetWithQueryParam.request(MediaType.TEXT_PLAIN_TYPE);
				invocationBuilder.header("some-header", "true");
		
		Response response =
			target.request(MediaType.TEXT_PLAIN_TYPE)
				.post(Entity.entity("A string entity to be POSTed", MediaType.TEXT_PLAIN));
		....
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	public void startContainer(String containerId) throws Exception {
		
		// Perform a docker 'run'.
		
		
		....
		
	}
	
	public void stopContainer(String containerId) throws Exception {

		// Perform a docker 'stop'.
		....
		
		
	}
	
	public void destroyContainer(String containerId) throws Exception {
		
		// Perform a docker 'rm'.
		
		....
	}
	
	public DockerContainer[] getContainers() throws Exception {
		
		return ....
	}
}
