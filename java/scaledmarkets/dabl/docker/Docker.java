package scaledmarkets.dabl.docker;

import java.net.URI;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Map;
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
	
	Docker privileged container:
	https://blog.docker.com/2013/09/docker-can-now-run-within-docker/
	(See more recent info on configuring container capilities.)
	
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
		String hostPathToMap, String containerPathToMap, boolean enableNetworking,
		Properties envVariables)
	throws Exception {
		
		return createContainer(imageIdOrName, containerName, new String[] {hostPathToMap},
			new String[] {containerPathToMap}, enableNetworking, envVariables);
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerCreate
	 */
	public DockerContainer createContainer(String imageIdOrName, String containerName,
		String[] hostPathsToMap, String[] containerPathsToMap, boolean enableNetworking,
		Properties envVariables)
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
		
		JsonArrayBuilder envVarBuilder = Json.createArrayBuilder();
		if (envVariables != null) {
			for (Object key : envVariables.keySet()) {
				
				if (! (key instanceof String)) throw new Exception(
					"Property key is not a String: " + key.toString());
				Object value = envVariables.get(key);
				if (! (value instanceof String)) throw new Exception(
					"Property value is not a String: " + value.toString());
				
				envVarBuilder.add((String)key + "=" + (String)value);
			}
		}
		
		JsonObjectBuilder healthcheck = Json.createObjectBuilder()
			.add("Test", Json.createArrayBuilder().add("NONE"))
			.add("Interval", 0)
			.add("Timeout", 0)
			.add("Retries", 0);
	
		JsonObject model = Json.createObjectBuilder()
			.add("Hostname", containerName)
			.add("Domainname", containerName)
			.add("User", "root")
			.add("ExposedPorts", ports)
			.add("Env", envVarBuilder)
			.add("Cmd", Json.createArrayBuilder())
			.add("Healthcheck", healthcheck)
			.add("ArgsEscaped", true)
			.add("Image", imageIdOrName)
			.add("Volumes", Json.createObjectBuilder())
			.add("WorkingDir", "/")
			.addNull("Entrypoint")
			.add("NetworkDisabled", ! enableNetworking)
			.add("MacAddress", "01:01:01:01:01:01")
			.add("OnBuild", Json.createArrayBuilder())
			.add("Labels", Json.createObjectBuilder())
			.add("Shell", Json.createArrayBuilder().add("bash"))
			.add("HostConfig", hostConfig)
			.add("NetworkingConfig", netConfig)
			// Optionals:
			.add("AttachStdin", false)
			.add("AttachStdout", false)
			.add("AttachStderr", false)
			.add("Tty", false)
			.add("OpenStdin", false)
			.add("StdinOnce", false)
			.build();
		
		StringWriter stWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
			jsonWriter.writeObject(model);
		}
		String jsonPayload = stWriter.toString();
		
		// Tell docker to create container, and get resulting container Id.
		Response response = makePostRequest(
			"v1.27/containers/create", jsonPayload,
				new String[] { "name", containerName } );
		
		System.out.println("response status=" + response.getStatus());
		System.out.println("response message=" + response.getStatusInfo().getReasonPhrase());  // debug
		
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
			"v1.27/containers/" + containerId + "/start", null);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Attach to container.
		// Ref: https://docs.docker.com/engine/api/v1.27/#operation/ContainerAttach
		
		response = makePostRequest(
			"v1.27/containers/" + containerId + "/attach", stdin,
			new String[] { "stream", "logs" },
			new String[] { "stdin", "true" },
			new String[] { "stdout", "true" },
			new String[] { "stderr", "true" }
			);
		
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
			"v1.27/containers/" + containerId + "/stop", null);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
	}
	
	/**
	 * Tell docker to remove container.
	 */
	public void destroyContainer(String containerId) throws Exception {
		
		System.out.println("Destroying conatiner " + containerId);  // debug
		Response response = makeDeleteRequest("v1.27/containers/" + containerId);
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
	}
	
	/**
	 * Remove all of the containers that match either the specified regular
	 * expression for the container name, or match (exactly) the specified
	 * label, or both.
	 */
	public void destroyContainers(String namePattern, String label) throws Exception {
		
		System.out.println("Destroying conatiners matching pattern " + namePattern);  // debug
		DockerContainer[] containers = null;
		try { containers = getContainers(namePattern, label); }
		catch (Exception ex) {
			return;
		}
		for (DockerContainer container : containers) {
			destroyContainer(container.getContainerId());
		}
	}
	
	/**
	 * Return true if the daemon reports that the specified container is running.
	 */
	public boolean containerIsRunning(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.27/containers/" + containerId + "/json");
		
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
	
	public boolean containerExited(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.27/containers/" + containerId + "/json");
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonObject json = (JsonObject)(reader.read());
		
		JsonObject state = json.getJsonObject("State");
		String status = state.getString("Status");
		return status.equals("exited");
	}
	
	/**
	 * Return true if the daemon reports that the specified container exists.
	 */
	public boolean containerExists(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.27/containers/" + containerId + "/json");
		
		if (response.getStatus() >= 500) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		if (response.getStatus() >= 400) return false;
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		return true;
	}
	
	public int getExitStatus(String containerId) throws Exception {
		
		Response response = makeGetRequest(
			"v1.27/containers/" + containerId + "/json");
		
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
			"v1.27/containers/json?all=true" + labelFilter);
		
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
	 * Return a list, for which each element of the list represents an image,
	 * and is a list of the names of that image.
	 * See https://docs.docker.com/engine/api/v1.27/#operation/ImageList
	 */
	public List<List<String>> getImages() throws Exception {
		
		Response response = makeGetRequest("v1.27/images/json");
		
		// Verify success and obtain container Id.
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Parse response.
		String responseBody = response.readEntity(String.class);
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		
		JsonArray jsonArray = (JsonArray)json;
		List<List<String>> images = new LinkedList<List<String>>();
		for (JsonValue value : jsonArray) {
			JsonObject imageDesc = (JsonObject)value;
			
			JsonArray tags = imageDesc.getJsonArray("RepoTags");
			List<String> names = new LinkedList<String>();
			for (JsonValue tag : tags) {
				names.add(tag.toString());
			}
			images.add(names);
		}
		return images;
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
	 * Perform a POST request to the docker daemon. Query parameters are provided
	 * as a Map, or may be null. body may be null.
	 */
	protected Response makePostRequest(String path, String body, String[]... params) {
		
		// debug
		System.out.println("Making post request to docker daemon: " + path);
		System.out.println("\twith params: ");
		for (String[] keyValuePair : params) {
			if (keyValuePair.length != 2) throw new RuntimeException(
				"Expected a key, value pair; found: " + keyValuePair.toString());
			System.out.println("\t\t" + keyValuePair[0] + "=" + keyValuePair[1]);
		}
		System.out.println("\tand payload: " + body);
		// end debug

		
		WebTarget target = this.endpoint.path(path);
		
		for (String[] keyValuePair : params) {
			if (keyValuePair.length != 2) throw new RuntimeException(
				"Expected a key, value pair; found: " + keyValuePair.toString());
			target = target.queryParam(keyValuePair[0], keyValuePair[1]);
		}
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		Response response;
		if (body == null) {
			response = invocationBuilder.post(null);
		} else {
			Entity<String> entity = Entity.json(body);
			Invocation invocation = invocationBuilder.buildPost(entity);
			response = invocation.invoke();
		}
		
		// debug
		System.out.println("Performed post request; return status is " + response.getStatus());
		// end debug
		
		
		return response;
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
