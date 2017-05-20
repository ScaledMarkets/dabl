package scaledmarkets.dabl.task;

public class DablStandard {
	public static final String PackageText = "package dabl.standard\n" +
		
		"//* Publish an artifact to a repository. \n" +
		"function post \n" +
			"\tstring /// the artifact to publish \n" +
			"\tto string /// destination repo \n" +
			"\tstring /// path in the destination repo \n" +
			"\tbinds to java method dabl.task.Standard.post \n" +
		
		"//* Display a message to stdout. \n" +
		"function report \n" +
			"\tstring /// the message to send to stdout \n" +
			"\tbinds to java method dabl.task.Standard.report \n" +
		
		"//* Execute a shell command. \n" +
		"function bash \n" +
			"\tstring /// the command to execute \n" +
			"\tbinds to java method dabl.task.Standard.bash \n" +
		
		"//* Execute a powershell command. \n" +
		"function powershell \n" +
			"\tstring /// the command to execute \n" +
			"\tbinds to java method dabl.task.Standard.powershell \n" +
		
		"//* Deploy an artifact, using the specified template file. \n" +
		"function deploy \n" +
			"\tstring /// template file \n" +
			"\tto string /// deployment \n" +
			"\tbinds to java method dabl.task.Standard.deploy \n" +
		
		"//* Execute a shell command via ssh. \n" +
		"function ssh \n" +
			"\tstring /// target host \n" +
			"\tstring /// command string \n" +
			"\tbinds to java method dabl.task.Standard.ssh \n" +
		
		"//* Destroy a deployed environment. \n" +
		"function destroy \n" +
			"\tstring /// deployment \n" +
			"\tbinds to java method dabl.task.Standard.destroy \n" +
	;
}
