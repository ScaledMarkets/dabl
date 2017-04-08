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
	
	JSON API:
	http://docs.oracle.com/javaee/7/tutorial/jsonp003.htm
	https://docs.oracle.com/javaee/7/api/toc.htm
	
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
	public static int Port = 22;

	private String dockerURL;
	private URI uri;
	private WebTarget endpoint;
	private Client client;
	private static String portStr = String.valueOf(Port);
	private static String hostPortStr = "22";
	
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
	
	public void close() throws Exception {
		this.client.close();
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
		
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
		return response.getMessage();
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerCreate
	 */
	public DockerContainer createContainer(String imageIdOrName, String containerName,
		String[] hostPathsToMap, String[] containerPathsToMap) throws Exception {
		
		if ((hostPathsToMap == null) || (containerPathsToMap == null))
			assertThat(hostPathsToMap == containerPathsToMap,
				"Only one of host path and container path arguments is null");
		
		JsonObject ports = Json.createObjectBuilder()
			.add(portStr + "/tcp", Json.createObjectBuilder());
		
		filesystemMap = Json.createArrayBuilder();
		
		if (hostPathsToMap != null) {
			assertThat(hostPathsToMap.length == containerPathsToMap.length,
				"Number of host filesystem paths must equal number of container filesystem paths");
		
			int i = 0;
			for (String hostPath : hostPathsToMap) {
				filesystemMap.add(hostPath + ":" + containerPathsToMap[i++]);
			}
		}
		
		JsonObject hostPortBindings = Json.createObjectBuilder()
			.add(portStr + "/tcp", Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
					.add("HostPort", hostPortStr)));
		
		
		JsonObject hostConfig = Json.createObjectBuilder()
			.add("Binds", filesystemMap)
			.add("PortBindings", hostPortBindings)
			.add("NetworkMode", "bridge");
		
		JsonObject netConfig = Json.createObjectBuilder();
	
		JsonObject model = Json.createObjectBuilder()
			.add("AttachStdin", false)
			.add("AttachStdout", false)
			.add("AttachStderr", false)
			.add("Tty", false)
			.add("OpenStdin", false)
			.add("StdinOnce", false)
			.add("Env", Json.createArrayBuilder())
			.add("Cmd", Json.createArrayBuilder())
			.add("Entrypoint", "")
			.add("Image", imageIdOrName)
			.add("Labels", Json.createObjectBuilder())
			.add("Volumes", Json.createObjectBuilder()
			.add("NetworkDisabled", false)
			.add("ExposedPorts", ports)
			.add("StopSignal", "SIGTERM")
			.add("HostConfig", hostConfig)
			.add("NetworkingConfig", netConfig)
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
		
		// Parse response and obtain the Id of the new container.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		String containerId = json.get("Id");
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	/**
	 * Tell docker to start container.
	 */
	public void startContainer(String containerId) throws Exception {
		
		Response response = makePostRequest(
			"v1.24/containers/" + containerId + "/start", null);
		
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
	}
	
	/**
	 * Tell docker to stop container.
	 */
	public void stopContainer(String containerId) throws Exception {

		Response response = makePostRequest(
			"v1.24/containers/" + containerId + "/stop", null);
		
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
	}
	
	/**
	 * Tell docker to remove container.
	 */
	public void destroyContainer(String containerId) throws Exception {
		
		Response response = makeDeleteRequest("v1.24/containers/" + containerId);
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
	}
	
	public boolean containerIsRunning(String containerId) throws Exception {
		
		....
	}
	
	public boolean containerExists(String containerId) throws Exception {
		
		....
	}
	
	public DockerContainer[] getContainers() throws Exception {
		
		Response response = makeGetRequest("v1.24/containers/json?all=true");
		
		// Verify success and obtain container Id.
		if (response.getStatus() >= 300) throw new Exception(response.getMessage());
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		
		JsonArray jsonArray = (JsonArray)json;
		DockerContainer[] containers = new DockerContainer[jsonArray.size()];
		int i = 0;
		for (JsonValue value : jsonArray) {
			JsonStructure containerDesc = (JsonStructure)value;
			String id = containerDesc.get("Id");
			containers[i++] = new DockerContainer(this, id);
		}
		
		return containers;
	}
	
	/**
	 * Remove all of the containers that match either the specified regular
	 * expression for the container name, or match (exactly) the specified
	 * label, or both.
	 */
	public void destroyContainers(String namePattern, String label) throws Exception {
		
		....
	}
	
	protected Response makeGetRequest(String path) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		return invocationBuilder.get();
	}
	
	/**
	 * body may be null.
	 */
	protected Response makePostRequest(String path, String body) {
		
		
		if (body != null) ....
		
	}

	protected Response makeDeleteRequest(String path) {
		
		
	}
}
