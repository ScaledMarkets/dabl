package test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Stream;

import org.json.*;
// https://github.com/stleary/JSON-java
// http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.json%22

public class Utils {
	
	public static String getSafeHarborHost() {
		String host = System.getProperty("SAFEHARBOR_HOST");
		if (host == null) {
			throw new RuntimeException("Variable SAFEHARBOR_HOST undefined");
		}
		return host;
	}
	
	public static int getSafeHarborPort() {
		String portStr = System.getProperty("SAFEHARBOR_PORT");
		if (portStr == null) {
			throw new RuntimeException("Variable SFEHARBOR_PORT undefined");
		}
		int port = Integer.parseInt(portStr);
		return port;
	}
	
	public static String[] makeRequest(String methodName, String... args) throws Exception
	{
		String[] commandAr = new String[args.length + 4];
		commandAr[0] = "bin/safeharborcmdclient";
		commandAr[1] = "-h=" + getSafeHarborHost();
		commandAr[2] = "-p=" + String.valueOf(getSafeHarborPort());
		commandAr[3] = methodName;
		
		int i = 4;
		for (String arg : args) {
			commandAr[i++] = arg;
		}
		
		Process process = Runtime.getRuntime().exec(commandAr);
		process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
		String[] responses = Utils.getResponse(process);
		if (process.exitValue() != 0) {
			throw new Exception(responses[0] + "; " + responses[1]);
		}
		return responses;
	}
	
	public static String[] getResponse(Process process) throws Exception {
		assertThat(process != null);
		
		//boolean ok = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);
		//assert ok;
		//if (! ok) { throw new Exception("timed out"); }
		InputStream os = process.getInputStream();
		assertThat(os != null);
		BufferedReader lineReader = new BufferedReader(new InputStreamReader(os));
		Stream<String> lines = lineReader.lines();
		String[] responseAr1 = { "" };
		lines.forEachOrdered(line -> responseAr1[0] += line);
		String stdout = responseAr1[0];
		
		InputStream es = process.getErrorStream();
		assertThat(es != null);
		lineReader = new BufferedReader(new InputStreamReader(es));
		lines = lineReader.lines();
		String[] responseAr2 = new String[]{ "" };
		lines.forEachOrdered(line -> responseAr2[0] += line);
		String stderr = responseAr2[0];
		
		return new String[] { stdout, stderr };
	}
	
	public static JSONObject parseResponses(String[] responses) throws Exception {
		JSONObject jSONObject;
		try {
			jSONObject = new JSONObject(responses[0]);
		} catch (Exception ex) {
			throw new Exception("stdout=" + responses[0] + ", stderr=" + responses[1], ex);
		}
		return jSONObject;
	}
	
	public static JSONObject getResponseAsJSON(Process process) throws Exception {
		String[] responses = getResponse(process);
		JSONObject json = new JSONObject(responses[0]);
		System.out.println("...done.");
		return json;
	}
	
	public static void assertThat(boolean expr) throws Exception {
		assertThat(expr, null);
	}
	
	public static void assertThat(boolean expr, String msg) throws Exception {
		if (msg != null) msg = "; " + msg;
		if (! expr) throw new Exception("Assertion violation" + msg);
	}
}
