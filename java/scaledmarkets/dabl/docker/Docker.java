package scaledmarkets.dabl.docker;

import java.net.URI;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonArrayBuilder;
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
	
	/** Normally, this is a unix socket URL. */
	public static String DefaultDockerURL = "unix:///var/run/docker.sock";
	
	/** Port to map to the containers that are created. */
	public static int ContainerPort = 22;

	private String dockerURL;
	private URI uri;
	private WebTarget endpoint;
	private Client client;
	private static String containerPortStr = String.valueOf(ContainerPort);
	private static String hostPortStr = "22";
	
	protected Docker(Client client, WebTarget endpoint) {
		this.client = client;
		this.endpoint = endpoint;
	}
	
	/**
	 * Create a Docker object for communicating with to a Docker daemon.
	 * This method does not actually communicate with the daemon. To test the
	 * connection, use the ping method.
	 */
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
		clientConfig.property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);

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
	
	/**
	 * Create a connection, but using the default unix URL.
	 */
	public static Docker connect() throws Exception {
		return connect(DefaultDockerURL);
	}
	
	/**
	 * Release all connection resources.
	 */
	public void close() throws Exception {
		this.client.close();
	}
	
	/**
	 * TBD: move this out of this package.
	 */
	public void validateRequiredConfiguration() throws Exception {
		// Verify that the required base image is present.
		//....
		
		
	}
	
	/**
	 * Verify that the docker daemon is running and can be accessed.
	 */
	public String ping() throws Exception {
		
		Response response = makeGetRequest("_ping");
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		return response.getStatusInfo().getReasonPhrase();
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerCreate
	 */
	public DockerContainer createContainer2(String imageIdOrName, String containerName,
		String hostPathToMap, String containerPathToMap, boolean enableNetworking)
	throws Exception {
		
		return createContainer(imageIdOrName, containerName, new String[] {hostPathToMap},
			new String[] {containerPathToMap}, enableNetworking);
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerCreate
	 */
	public DockerContainer createContainer(String imageIdOrName, String containerName,
		String[] hostPathsToMap, String[] containerPathsToMap, boolean enableNetworking)
	throws Exception {
		
		if ((hostPathsToMap == null) || (containerPathsToMap == null))
			if (hostPathsToMap != containerPathsToMap) throw new Exception(
				"Only one of host path and container path arguments is null");
		
		JsonObjectBuilder ports = Json.createObjectBuilder()
			.add(containerPortStr + "/tcp", Json.createObjectBuilder());
		
		JsonArrayBuilder filesystemMap = Json.createArrayBuilder();
		
		if (hostPathsToMap != null) {
			if (hostPathsToMap.length != containerPathsToMap.length) throw new Exception(
				"Number of host filesystem paths must equal number of container filesystem paths");
		
			int i = 0;
			for (String hostPath : hostPathsToMap) {
				filesystemMap.add(hostPath + ":" + containerPathsToMap[i++]);
			}
		}
		
		JsonObjectBuilder hostPortBindings = Json.createObjectBuilder()
			.add(containerPortStr + "/tcp", Json.createArrayBuilder()
				.add(Json.createObjectBuilder()
					.add("HostPort", hostPortStr)));
		
		JsonObjectBuilder hostConfig = Json.createObjectBuilder()
			.add("Binds", filesystemMap)
			.add("PortBindings", hostPortBindings)
			.add("NetworkMode", "bridge");
		
		JsonObjectBuilder netConfig = Json.createObjectBuilder();
	
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
			.add("Volumes", Json.createObjectBuilder())
			.add("NetworkDisabled", ! enableNetworking)
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
			"v1.24/containers/create?name=" + containerName,
			jsonPayload);
		
		// Verify success and obtain container Id.
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Parse response and obtain the Id of the new container.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		String containerId = ((JsonObject)json).getString("Id");
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	/**
	 * Tell docker to start container, and pass it a string for its stdin.
	 */
	public InputStream startContainer(String containerId, String stdin) throws Exception {
		
		Response response = makePostRequest(
			"v1.24/containers/" + containerId + "/start", null);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Attach to container.
		// Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerAttach
		
		String params = "?";
		params = params + "stream=logs";
		params = params + "&stdin=true";
		params = params + "&stdout=true";
		params = params + "&stderr=true";

		response = makePostRequest(
			"v1.24/containers/" + containerId + "/attach" + params, stdin);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Connect response output stream to 
		
		InputStream inputStream = response.readEntity(InputStream.class);
		
		return inputStream;
	}
	
	/**
	 * Tell docker to stop container.
	 */
	public void stopContainer(String containerId) throws Exception {

		Response response = makePostRequest(
			"v1.24/containers/" + containerId + "/stop", null);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
	}
	
	/**
	 * Tell docker to remove container.
	 */
	public void destroyContainer(String containerId) throws Exception {
		
		Response response = makeDeleteRequest("v1.24/containers/" + containerId);
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
	}
	
	/**
	 * Remove all of the containers that match either the specified regular
	 * expression for the container name, or match (exactly) the specified
	 * label, or both.
	 */
	public void destroyContainers(String namePattern, String label) throws Exception {
		
		DockerContainer[] containers = getContainers(namePattern, label);
		for (DockerContainer container : containers) {
			destroyContainer(container.getContainerId());
		}
	}
	
	/**
	 * Return true if the daemon reports that the specified container is running.
	 */
	public boolean containerIsRunning(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.24/containers/" + containerId + "/json");
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonObject json = (JsonObject)(reader.read());
		
		JsonObject state = json.getJsonObject("State");
		boolean running = state.getBoolean("Running");
		return running;
	}
	
	/**
	 * Return true if the daemon reports that the specified container exists.
	 */
	public boolean containerExists(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.24/containers/" + containerId + "/json");
		
		if (response.getStatus() >= 500) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		if (response.getStatus() >= 400) return false;
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		return true;
	}
	
	public int getExitStatus(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.24/containers/" + containerId + "/json");
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Obtain the "State" JSON object, and from that obtain the
		// "Status" and the ExitCode.
		/*
		"State": 
		{
			"Error": "",
			"ExitCode": 9,
			"FinishedAt": "2015-01-06T15:47:32.080254511Z",
			"OOMKilled": false,
			"Dead": false,
			"Paused": false,
			"Pid": 0,
			"Restarting": false,
			"Running": true,
			"StartedAt": "2015-01-06T15:47:32.072697474Z",
			"Status": "running"
		},
		*/
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonObject json = (JsonObject)(reader.read());
		
		JsonObject state = json.getJsonObject("State");
		boolean running = state.getBoolean("Running");
		
		if (running) throw new Exception("Container is running");
		
		try {
			int exitCode = state.getInt("ExitCode");
			return exitCode;
		} catch (NullPointerException ex) {
			throw new Exception("No value found for ExitCode", ex);
		}
	}
	
	/**
	 * Obtain the containers with the specified name pattern or label. Pattern
	 * matching is done using the Java regex Pattern methods.
	 * Label may be a key name, or a key=value string.
	 */
	public DockerContainer[] getContainers(String namePattern, String label) throws Exception {
		
		String labelFilter = "";
		if (label != null) labelFilter = "&filters={\"label\": [" + label + "]}";
		
		Response response = makeGetRequest(
			"v1.24/containers/json?all=true" + labelFilter);
		
		// Verify success and obtain container Id.
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		
		JsonArray jsonArray = (JsonArray)json;
		List<DockerContainer> containers = new LinkedList<DockerContainer>();
		for (JsonValue value : jsonArray) {
			JsonObject containerDesc = (JsonObject)value;
			
			JsonArray names = containerDesc.getJsonArray("Names");
			if (namePattern != null) {
				for (JsonValue v : names) {
					String name = ((JsonString)v).getString();
					if (Pattern.matches(namePattern, name)) {
						String id = containerDesc.getString("Id");
						containers.add(new DockerContainer(this, id));
						break;
					}
				}
			} else {
				String id = containerDesc.getString("Id");
				containers.add(new DockerContainer(this, id));
			}
		}
		
		return containers.toArray(new DockerContainer[containers.size()]);
	}
	
	/**
	 * Return the containers known to the daemon.
	 */
	public DockerContainer[] getContainers() throws Exception {
		return getContainers(null, null);
	}
	
	/**
	 * Perform a GET request to the docker daemon.
	 */
	protected Response makeGetRequest(String path) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		return invocationBuilder.get();
	}
	
	/**
	 * Perform a POST request to the docker daemon.
	 * body may be null.
	 */
	protected Response makePostRequest(String path, String body) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		if (body == null) {
			return invocationBuilder.post(null);
		} else {
			Entity<String> entity = Entity.json(body);
			Invocation invocation = invocationBuilder.buildPost(entity);
			return invocation.invoke();
		}
	}

	/**
	 * Perform a DELETE request to the docker daemon.
	 */
	protected Response makeDeleteRequest(String path) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		return invocationBuilder.delete();
	}
}
