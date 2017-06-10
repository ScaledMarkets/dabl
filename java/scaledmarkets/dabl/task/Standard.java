package dabl.task;

/**
 * Implement the functions that are defined by DABL built-in package dabl.standard,
 * which is defined in class DablStandard. See DablStandard.PackageText for the
 * definition of each function that is implemented here.
 */
public class Standard {

	public static void report(String message) {
		System.out.println(message);
	}
	
	/**
	 * Create a process, run bash in the process, and pass the command string to it.
	 * If the process ends with a non-zero status, throw a RuntimeException.
	 */
	public static void bash(String commandString, long timeoutSeconds) {
		
		Process process;
		try { process = Runtime.exec("bash " + commandString); }
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
		// Get output streams of the process.
		InputStream inputStream = new BufferedInputStream(process.getInputStream());
		InputStream errorStream = new BufferedInputStream(process.getErrorStream());
		
		// Pipe the above to stdout and stderr.
		Utilities.pipeInputStreamToOutputStream(inputStream, System.out);
		Utilities.pipeInputStreamToOutputStream(errorStream, System.err);
		
		// Wait for the process to complete. Note that it might already be done.
		boolean completedNormally = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
		if (! completedNormally) {
			throw new RuntimeException("Process timed out");
		}
		
		int exitStatus = process.exitValue();
		if (exitStatus != 0) {
			throw new RuntimeException("Process exited with status " + exitStatus);
		}
	}
	
	public static void powershell(String commandString) {
		
		// ....
		throw new RuntimeException("powershell not implemented yet");
	}
	
	public static void deploy(String templateFileFef, String deploymentRef) {
		
		// ....
		throw new RuntimeException("deploy not implemented yet");
	}
	
	public static void ssh(String targetHost, String commandString ) {
		
		// ....
		throw new RuntimeException("ssh not implemented yet");
	}
	
	public static void destroy(String deploymentRef) {
		
		// ....
		throw new RuntimeException("destroy not implemented yet");
	}
}
