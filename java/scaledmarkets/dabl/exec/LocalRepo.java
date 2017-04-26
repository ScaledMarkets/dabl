package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A DABL "local repository" is a file storage area (e.g., directory) available
 * on the host system that is running the DABL processor. Its purpose is to
 * store intermediate build artifacts. A local repository is isolated from other
 * local repositories.
 */
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
		if ((workingDirPath == null) || (workingDirPath.equals("")))
			workingDirPath = System.getProperty("user.dir");
		
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
	 * Retrieve the specified files from this repository and copy them to 'dir'.
	 */
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		patternSets.operateOnFiles(this.directory, new PatternSets.FileOperator() {
			public void op(File root, String pathRelativeToRoot) throws Exception {
				// Get the file
				File origin = new File(LocalRepo.this.directory, pathRelativeToRoot);
				File dest = new File(dir, pathRelativeToRoot);
				Files.copy(origin.toPath(), dest.toPath());
			}
		});
	}
	
	/**
	 * Store ("push") the specified files from 'dir' to this specified repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		patternSets.operateOnFiles(this.directory, new PatternSets.FileOperator() {
			public void op(File root, String pathRelativeToRoot) throws Exception {
				// Put the file
				File origin = new File(dir, pathRelativeToRoot);
				File dest = new File(LocalRepo.this.directory, pathRelativeToRoot);
				Files.copy(origin.toPath(), dest.toPath());
			}
		});
	}

	public List<String> listFiles(String dirpath) throws Exception {
		....
	}
	
	public List<String> listFilesRecursively(String dirpath) throws Exception {
		....
	}
	
	public List<String> listFiles() throws Exception {
		....
	}
	
	public List<String> listFilesRecursively() throws Exception {
		....
	}
	
	public boolean containsFile(String filepath) throws Exception {
		....
	}
	
	public int countFiles() throws Exception {
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
