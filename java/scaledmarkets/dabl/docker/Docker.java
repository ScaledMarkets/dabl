package scaledmarkets.dabl.docker;

import java.net.URI;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
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
	https://docs.docker.com/engine/api/v1.30/
	
	Docker privileged container:
	https://blog.docker.com/2013/09/docker-can-now-run-within-docker/
	(See more recent info on configuring container capilities.)
	
	JSON API:
	http://docs.oracle.com/javaee/7/tutorial/jsonp003.htm
	https://docs.oracle.com/javaee/7/api/toc.htm
	
	Jersey API reference:
	https://jersey.github.io/apidocs/1.19.1/jersey/index.html
	https://docs.oracle.com/javaee/7/api/
	
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
	
	private static final String DockerEngineAPIVersion = "v1.30";
	
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
		response.close();
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		return response.getStatusInfo().getReasonPhrase();
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.30/#operation/ContainerCreate
	 */
	public DockerContainer createContainer2(String imageIdOrName, String containerName,
		String hostPathToMap, String containerPathToMap, boolean enableNetworking,
		boolean connectOnStart, Properties envVariables)
	throws Exception {
		
		return createContainer(imageIdOrName, containerName, new String[] {hostPathToMap},
			new String[] {containerPathToMap}, enableNetworking, connectOnStart, envVariables);
	}
	
	/**
	 * 
	 * Ref: https://docs.docker.com/engine/api/v1.30/#operation/ContainerCreate
	 */
	public DockerContainer createContainer(String imageIdOrName, String containerName,
		String[] hostPathsToMap, String[] containerPathsToMap, boolean enableNetworking,
		boolean connectOnStart, Properties envVariables)
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
			.add("AttachStdin", connectOnStart)
			.add("AttachStdout", connectOnStart)
			.add("AttachStderr", connectOnStart)
			.add("Tty", false)
			.add("OpenStdin", true)
			.add("StdinOnce", true)
			.build();
		
		StringWriter stWriter = new StringWriter();
		try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
			jsonWriter.writeObject(model);
		}
		String jsonPayload = stWriter.toString();
		
		// Tell docker to create container, and get resulting container Id.
		String containerId;
		Response response = null;
		try {
			response = makePostRequest(
				DockerEngineAPIVersion + "/containers/create", MediaType.APPLICATION_JSON_TYPE,
					jsonPayload, new String[] { "name", containerName } );
			
			System.out.println("response status=" + response.getStatus());
			System.out.println("response message=" + response.getStatusInfo().getReasonPhrase());  // debug
			
			// Verify success and obtain container Id.
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			// Parse response and obtain the Id of the new container.
			String responseBody = response.readEntity(String.class);
			JsonReader reader = Json.createReader(new StringReader(responseBody));
			JsonStructure json = reader.read();
			containerId = ((JsonObject)json).getString("Id");
		} finally {
			response.close();
		}
		
		// Wrap the container Id in an object that we can return.
		DockerContainer container = new DockerContainer(this, containerId);
		
		return container;
	}
	
	/**
	 * Tell docker to start container.
	 */
	public void startContainer(String containerId) throws Exception {
		
		Response response = null;
		try {
			response = makePostRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/start", null, null);
			
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
		} finally {
			response.close();
		}
	}
	
	/**
	 * Tell docker to start container, and connect to the container's input and output.
	 * It is assumed that the container was created with AttachStdin, AttachStdout,
	 * and AttachStderr set to true.
	 * This method will block until the container has read all of its input.
	 */
	public InputStream startContainer(String containerId, InputStream input) throws Exception {
		
		// Read all of the input and forward it to the container.
		String inputString = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		for (;;) {
			String line = br.readLine();
			if (line == null) break;
			inputString = inputString + line;
		}
		
		Response response = null;
		response = makePostRequest(
			DockerEngineAPIVersion + "/containers/" + containerId + "/start", null, null);
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Return an input stream for reading the container's output.
		return response.readEntity(InputStream.class);
	}
	
	/**
	 * Attach to container.
	 * Ref: https://docs.docker.com/engine/api/v1.30/#operation/ContainerAttach
	 * This method will block until the container has read all of its input.
	 */
	public InputStream connectToContainer(String containerId, InputStream input) throws Exception {
		
		// Read all of the input and forward it to the container.
		String inputString = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		for (;;) {
			String line = br.readLine();
			if (line == null) break;
			inputString = inputString + line;
		}
		
		Response response = makePostRequest(
			DockerEngineAPIVersion + "/containers/" + containerId + "/attach", 
			MediaType.TEXT_PLAIN_TYPE, inputString,
			new String[] { "stream", "logs" },
			new String[] { "stdin", "true" },
			new String[] { "stdout", "true" },
			new String[] { "stderr", "true" }
			);
		
		System.out.println("Attached to " + containerId); // debug
		
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		
		// Return an input stream for reading the container's output.
		return response.readEntity(InputStream.class);
	}
	
	/**
	 * Tell docker to stop container.
	 */
	public void stopContainer(String containerId) throws Exception {

		System.out.println("Stopping container " + containerId);  // debug
		Response response = null;
		try {
			response = makePostRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/stop", null, null);
			
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
		} finally {
			response.close();
		}
	}
	
	/**
	 * Tell docker to remove container.
	 */
	public void destroyContainer(String containerId) throws Exception {
		
		if (containerIsRunning(containerId)) stopContainer(containerId);
		
		System.out.println("Destroying container " + containerId);  // debug
		Response response = null;
		try {
			response = makeDeleteRequest(
			DockerEngineAPIVersion + "/containers/" + containerId);
		if (response.getStatus() >= 300) throw new Exception(
			response.getStatusInfo().getReasonPhrase());
		} finally {
			response.close();
		}
	}
	
	/**
	 * Remove all of the containers that match either the specified regular
	 * expression for the container name, or match (exactly) the specified
	 * label, or both. Return the number of containers that were destroyed.
	 */
	public int destroyContainers(String namePattern, String label) throws Exception {
		
		System.out.println("Destroying conatiners matching pattern " + namePattern);  // debug
		DockerContainer[] containers = getContainers(namePattern, label);
		System.out.println("Destroying " + containers.length + " containers...");
		int numOfContainersDestroyed = 0;
		for (DockerContainer container : containers) {
			destroyContainer(container.getContainerId());
			numOfContainersDestroyed++;
		}
		System.out.println("...containers destroyed.");
		return numOfContainersDestroyed;
	}
	
	/**
	 * Return true if the daemon reports that the specified container is running.
	 */
	public boolean containerIsRunning(String containerId) throws Exception {
		
		Response response = null;
		JsonObject json;
		try {
			response = makeGetRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/json");
		
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			// Parse response.
			String responseBody = response.readEntity(String.class);
			JsonReader reader = Json.createReader(new StringReader(responseBody));
			json = (JsonObject)(reader.read());
		} finally {
			response.close();
		}
		
		JsonObject state = json.getJsonObject("State");
		boolean running = state.getBoolean("Running");
		return running;
	}
	
	public boolean containerExited(String containerId) throws Exception {
		
		Response response = null;
		JsonObject json;
		try {
			response = makeGetRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/json");
		
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			// Parse response.
			String responseBody = response.readEntity(String.class);
			JsonReader reader = Json.createReader(new StringReader(responseBody));
			json = (JsonObject)(reader.read());
		} finally {
			response.close();
		}
		
		JsonObject state = json.getJsonObject("State");
		String status = state.getString("Status");
		return status.equals("exited");
	}
	
	/**
	 * Return true if the daemon reports that the specified container exists.
	 */
	public boolean containerExists(String containerId) throws Exception {
		
		Response response = null;
		try {
			response = makeGetRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/json");
		
			if (response.getStatus() >= 500) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			if (response.getStatus() >= 400) return false;
			
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
		} finally {
			response.close();
		}
		
		return true;
	}
	
	public int getExitStatus(String containerId) throws Exception {
		
		Response response = null;
		String responseBody;
		try {
			response = makeGetRequest(
				DockerEngineAPIVersion + "/containers/" + containerId + "/json");
		
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
			
			responseBody = response.readEntity(String.class);
		} finally {
			response.close();
		}
		
		// Parse response.
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
	 * To list all containers using curl:
	 *	curl --unix-socket /var/run/docker.sock -X GET "http:/v1.24/containers/json?all=true"
	 * Docker code is at:
	 	https://github.com/moby/moby/blob/master/api/server/httputils/httputils.go
	 	https://github.com/moby/moby/blob/ff4f700f74450018f36d014f3cde0ff1b9c17fb3/api/server/router/container/container_routes.go
	 	https://github.com/moby/moby/blob/f96d45dc8ac21db1f082230e2f828a86e15cad46/client/container_list.go
	 	https://github.com/moby/moby/blob/f96d45dc8ac21db1f082230e2f828a86e15cad46/api/types/filters/parse.go
	 	https://github.com/moby/moby/blob/ff4f700f74450018f36d014f3cde0ff1b9c17fb3/client/request.go
	 */
	public DockerContainer[] getContainers(String namePattern, String label) throws Exception {
		
		String labelFilter = "";
		if (label != null) labelFilter = "\"label\":[" + label + "]";
		
		Response response = null;
		String responseBody;
		try {
			response = makeGetRequest(DockerEngineAPIVersion + "/containers/json",
				new String[] { "all", "true" } );
		
			// Verify success and obtain container Id.
			if (response.getStatus() == 404) { // not an error - means no containers found
				return new DockerContainer[] {};
			}
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			responseBody = response.readEntity(String.class);
		} finally {
			response.close();
		}
		
		// Parse response.
		JsonReader reader = Json.createReader(new StringReader(responseBody));
		JsonStructure json = reader.read();
		
		List<DockerContainer> containers = new LinkedList<DockerContainer>();
		JsonArray jsonArray = (JsonArray)json;
		for (JsonValue value : jsonArray) {
			JsonObject containerDesc = (JsonObject)value;
			
			JsonArray names = containerDesc.getJsonArray("Names");
			if (namePattern != null) {
				for (JsonValue v : names) {
					String name = ((JsonString)v).getString();
					System.out.println("namePattern=" + namePattern); // debug
					System.out.println("name=" + name); // debug
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
	 * See https://docs.docker.com/engine/api/v1.30/#operation/ImageList
	 */
	public List<List<String>> getImages() throws Exception {
		
		Response response = null;
		String responseBody;
		try {
			response = makeGetRequest(DockerEngineAPIVersion + "/images/json");
		
			// Verify success and obtain container Id.
			if (response.getStatus() >= 300) throw new Exception(
				response.getStatusInfo().getReasonPhrase());
			
			responseBody = response.readEntity(String.class);
		} finally {
			response.close();
		}
		
		// Parse response.
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
	 * IMPORTANT: It is essential to call close() on the Response object.
	 */
	protected Response makeGetRequest(String path, String[]... params) {
		
		// debug
		System.out.println("Making get request to docker daemon: " + path);
		System.out.println("\twith params: ");
		for (String[] keyValuePair : params) {
			if (keyValuePair.length != 2) throw new RuntimeException(
				"Expected a key, value pair; found: " + keyValuePair.toString());
			System.out.println("\t\t" + keyValuePair[0] + "=" + keyValuePair[1]);
		}
		// end debug

		
		WebTarget target = this.endpoint.path(path);
		
		for (String[] keyValuePair : params) {
			if (keyValuePair.length != 2) throw new RuntimeException(
				"Expected a key, value pair; found: " + keyValuePair.toString());
			target = target.queryParam(keyValuePair[0], keyValuePair[1]);
		}
		
		System.out.println("\t...creating invocation builder...");  // debug
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		System.out.println("\t...performing invocation...");  // debug
		Response response = invocationBuilder.buildGet().invoke();
		
		// debug
		System.out.println("Performed get request; return status is " + response.getStatus());
		// end debug
		
		return response;
	}
	
	/**
	 * Perform a POST request to the docker daemon. Query parameters are provided
	 * as a Map, or may be null. body may be null.
	 * IMPORTANT: It is essential to call close() on the Response object.
	 */
	protected Response makePostRequest(String path, MediaType contentType,
		String body, String[]... params) {
		
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
		
		Response response = null;
		if (body == null) {
			response = invocationBuilder.buildPost(null).invoke();
		} else {
			Entity<String> entity = Entity.entity(body, contentType);
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
	 * IMPORTANT: It is essential to call close() on the Response object.
	 */
	protected Response makeDeleteRequest(String path) {
		
		WebTarget target = this.endpoint.path(path);
		
		Invocation.Builder invocationBuilder =
			target.request(MediaType.TEXT_PLAIN_TYPE);
		
		return invocationBuilder.delete();
	}
}
