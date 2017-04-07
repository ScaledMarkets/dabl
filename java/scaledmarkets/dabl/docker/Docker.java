package scaledmarkets.dabl.docker;

import java.net.URI;
import java.io.StringWriter;
import java.io.StringReader;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonString;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.Registry;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.CommonProperties;

/*
	Docker REST API reference:
	https://docs.docker.com/engine/api/v1.27/
	
	JSON API guide:
	http://docs.oracle.com/javaee/7/tutorial/jsonp003.htm
	
	Jersey API reference:
	https://jersey.java.net/apidocs/latest/jersey/index.html
	
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
	
	public static String DefaultDockerURL = "unix:///var/run/docker.sock";

	private String dockerURL;
	private URI uri;
	private WebTarget endpoint;
	private Client client;
	
	public static Docker connect(String dockerURL) throws Exception {
		
		URI originalUri = new URI(dockerURL);
		URI uri = originalUri;
		if (originalUri.getScheme().equals("unix")) {
			uri = UnixConnectionSocketFactory.sanitizeUri(originalUri);
		}
		
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
			.register("unix", new UnixConnectionSocketFactory(originalUri))
			.build();
		
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.connectorProvider(new ApacheConnectorProvider());
		clientConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE,
				true);

		PoolingHttpClientConnectionManager connManager =
			new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		//clientConfig.register(ResponseStatusExceptionFilter.class);
		//clientConfig.register(JsonClientFilter.class);
		//clientConfig.register(JacksonJsonProvider.class);
		clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connManager);
	
		ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);
		
		Client client = clientBuilder.build();
		
		Docker docker = new Docker(client, client.target(uri));

		docker.validateRequiredConfiguration();
		
		return docker;
	}
	
	public static Docker connect() throws Exception {
		return connect(DefaultDockerURL);
	}
	
	protected Docker(Client client, WebTarget endpoint) {
		this.client = client;
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
		
		Response response = makeRequest("_ping");
		
		System.out.println(response.getStatus());
		return String.valueOf(response.getStatus());
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerCreate
	 */
	public DockerContainer createContainer(String imageId, String containerName) throws Exception {
		
		JsonObject model = Json.createObjectBuilder()
			.add("Hostname", "")
			.add("Domainname", )
			.add("User", )
			.add("AttachStdin", )
			.add("AttachStdout", )
			.add("AttachStderr", )
			.add("Tty", false)
			.add("OpenStdin", false)
			.add("StdinOnce", false)
			.add("Env", Json.createArrayBuilder()
				
				)
			.add("Cmd", )
			.add("Entrypoint", )
			.add("Image", )
			.add("Labels", )
			.add("Volumes", )
			.add("WorkingDir", )
			.add("NetworkDisabled", false)
			.add("MacAddress", )
			.add("ExposedPorts", )
			.add("StopSignal", "SIGTERM")
			.add("StopTimeout", )
			.add("HostConfig", )
			.add("NetworkingConfig", )
			.build();
		
		StringWriter stWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
			jsonWriter.writeObject(model);
		}
		String jsonPayload = stWriter.toString();	
		
		// Tell docker to create container, and get resulting container Id.
		Response response = makePostRequest(
			"v1.24/containers/" + imageId + "/create?name=" + containerName,
			jsonPayload);
		
		// Verify success and obtain container Id.
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
		
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		String containerId = json.get("Id");
		
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
	
	protected Response makeGetRequest(String path) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		return invocationBuilder.get();
	}
	
	protected Response makePostRequest(String path, String body) {
		
	}
}
