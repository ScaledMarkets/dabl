package scaledmarkets.dabl.docker;

import javax.ws.rs.core.*;
import javax.ws.rs.client.*;
import java.net.URI;

//import org.glassfish.jersey.client.ClientConfig;

/*
	https://github.com/oleg-nenashev/docker-java-1.6/blob/master/src/main/java/com/github/dockerjava/jaxrs/UnixConnectionSocketFactory.java
	https://github.com/oleg-nenashev/docker-java-1.6/blob/master/src/main/java/com/github/dockerjava/jaxrs/DockerCmdExecFactoryImpl.java
	https://github.com/kohlschutter/junixsocket
	org.glassfish.jersey.apache.connector.ApacheConnectorProvider
 */

/**
 * Provide access to a docker daemon.
 * This is not a full docker client: it provides only the access that is needed
 * by DABL.
 */
public class Docker {
	
	private String dockerURL;
	private URI uri;
	private WebTarget endpoint;
	public static String DefaultDockerURL = "unix:///var/run/docker.sock";
	
	public static Docker connect(String dockerURL) throws Exception {
		
		Client client = ClientBuilder.newClient();
		URI originalUri = new URI(dockerURL);
		URI uri;
		if (originalUri.getScheme().equals("unix")) {
			uri = UnixConnectionSocketFactory.sanitizeUri(originalUri);
		} else {
			uri = originalUri;
		}
		
		WebTarget endpoint = client.target(uri);
		Docker docker = new Docker(endpoint);
		
		docker.validateRequiredConfiguration();
		
		return docker;
	}
	
	public static Docker connect() throws Exception {
		return connect(DefaultDockerURL);
	}
	
	protected Docker(WebTarget endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * 
	 */
	public void validateRequiredConfiguration() throws Exception {
		// Verify that the required base image is present.
		//....
		
		
	}
	
	/**
	 * Verify that the docker daemon is running and can be accessed.
	 */
	public String ping() throws Exception {
		
		WebTarget target = this.endpoint.path("_ping");
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		Response response = invocationBuilder.get();

		System.out.println(response.getStatus());
		return String.valueOf(response.getStatus());
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
		//....
		
		String containerId = null; //....parse response
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	public void startContainer(String containerId, String[] pathsToMap) throws Exception {
		
		// Perform a docker 'run'.
		
		
		//....
		
	}
	
	public void stopContainer(String containerId) throws Exception {

		// Perform a docker 'stop'.
		//....
		
		
	}
	
	public void destroyContainer(String containerId) throws Exception {
		
		// Perform a docker 'rm'.
		
		//....
	}
	
	public DockerContainer[] getContainers() throws Exception {
		
		return null;
	}
}
