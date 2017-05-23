package dabl.task;

/**
 * Implement the functions that are defined by DABL built-in package dabl.standard,
 * which is defined in class DablStandard.
 */
public class Standard {


	public static void post(String artifact, String destinationRepo, String path) {
		
		....
	}
	
	public static void report(String message) {
		
		....
	}
	
	/**
	 * Create a process, run bash in the process, and pass the command string to it.
	 * If the process ends with a non-zero status, throw a RuntimeException.
	 */
	public static void bash(String commandString) {
		
		....
	}
	
	public static void powershell(String commandString) {
		
		....
	}
	
	public static void deploy(String templateFileFef, String deploymentRef) {
		
		....
	}
	
	public static void ssh(String targetHost, String commandString ) {
		
		....
	}
	
	public static void destroy(String deploymentRef) {
		
		....
	}
}
