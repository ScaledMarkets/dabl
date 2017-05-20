package scaledmarkets.dabl.analyzer;

public class DablStandard {
	public static final String PackageText = "package dabl.standard\n" +
		
		"//* Publish an artifact to a repository. \n" +
		"function post \n" +
			"\tstring /// the artifact to publish \n" +
			"\tto string /// destination repo \n" +
			"\tstring /// path in the destination repo \n" +
			"\tbinds to java method dabl.task.Standard.post\n" +
		
		"function report string \n" +
			"\tbinds to java method dabl.task.Standard.report\n" +
		
		"function bash string \n" +
			"\tbinds to java method dabl.task.Standard.bash\n" +
		
		"function powershell string \n" +
			"\tbinds to java method dabl.task.Standard.powershell\n" +
		
		"function deploy string to string \n" +
			"\tbinds to java method dabl.task.Standard.deploy\n" +
		
		"function ssh string string string \n" +
			"\tbinds to java method dabl.task.Standard.ssh\n" +
		
		"function destroy string \n" +
			"\tbinds to java method dabl.task.Standard.destroy\n" +
	;
}
