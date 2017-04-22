package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.io.File;

public class LocalRepo implements Repo {
	
	/**
	 * Create a local directory for the output to be written to, and return a
	 * wrapper Repo object for that directory.
	 */
	public static LocalRepo createRepo(String namespaceName, String taskName,
		String outputName, ALocalOartifactSet artifactSet) throws Exception {
		
		if (outputName == null) throw new Exception("Output name is null");
		if (outputName.equals("")) throw new Exception("Empty output name");
		
		String workingDirPath = Utilities.getSetting("dabl.local_repository_dir");
		File workingDir = new File(workingDirPath);
		if (! workingDir.exists()) throw new RuntimeException(
			"Directory " + workingDir.toString() + " not found");
		
		File namespaceDir = new File(workingDir, namespaceName);
		File taskDir = new File(namespaceDir, taskName);
		File repoDir = new File(taskDir, outputName);
		
		// Delete directory (and all contents) if it already exists.
		if (repoDir.exists()) {
			Utilities.deleteDirectoryTree(repoDir.toPath());
		}
		
		// Create directory for the outputs.
		boolean created = repoDir.mkdirs();
		if (! created) throw new Exception(
			"Unable to create directory " + repoDir.toString());
		
		// Set permissions so that only the owner can access.
		repoDir.setReadable(true, true);
		repoDir.setWritable(true, true);
		repoDir.setExecutable(true, true);
		
		LocalRepo repo = new LocalRepo(outputName, repoDir, artifactSet);
		return repo;
	}
	
	public String getOutputName() { return outputName; }
	
	public ALocalOartifactSet getArtifactSet() { return artifactSet; }

	/**
	 * Retrieve the specified files from the repository.
	 */
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		....
	}
	
	/**
	 * Store ("push") the specified files to the specified repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		....
	}

	private String outputName;
	private ALocalOartifactSet artifactSet;
	private File directory;
	
	protected LocalRepo(String outputName, File directory, ALocalOartifactSet artifactSet) {
		this.outputName = outputName;
		this.directory = directory;
		this.artifactSet = artifactSet;
	}
}
