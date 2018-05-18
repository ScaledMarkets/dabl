package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Date;
import java.util.Calendar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileVisitOption;
import java.nio.file.DirectoryStream;

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
				File origin = new File(root, pathRelativeToRoot);
				File dest = new File(dir, pathRelativeToRoot);
				Files.copy(origin.toPath(), dest.toPath());
			}
		});
	}
	
	/**
	 * Store ("push") the specified files from 'dir' to this repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		patternSets.operateOnFiles(dir, new PatternSets.FileOperator() {
				
			public void op(File root, String pathRelativeToRoot) throws Exception {

				// Put the file
				File origin = new File(root, pathRelativeToRoot);
				File dest = new File(LocalRepo.this.directory, pathRelativeToRoot);
				
				if (origin.isDirectory()) {
					dest.mkdir();
				} else {
					try {
						Files.copy(origin.toPath(), dest.toPath());
					} catch (Exception ex) {
						System.out.println("Origin:" + origin.toString());
						System.out.println("Origin dir:");
						Utilities.printDirectoryTree(root);
						System.out.println("Dest:" + dest.toString());
						System.out.println("Dest dir:");
						Utilities.printDirectoryTree(LocalRepo.this.directory);
						throw ex;
					}
				}
			}
		});
	}

	public List<String> listFiles(String dirpath) throws Exception {
		File dir = new File(this.directory, dirpath);
		if (! dir.exists()) throw new Exception(dirpath + " not found");
		if (! dir.isDirectory()) throw new Exception(dirpath + " is not a directory");
		return Arrays.asList(dir.list());
	}
	
	public void listFilesRecursively(List<String> files, String dirpath) throws Exception {
		File dir = new File(this.directory, dirpath);
		if (! dir.exists()) throw new Exception(dirpath + " not found");
		if (! dir.isDirectory()) throw new Exception(dirpath + " is not a directory");
		
		DirectoryStream<Path> paths = Files.newDirectoryStream(dir.toPath());
		for (Path path : paths) {
			String filename = path.getFileName().toString();
			String newDirpath = dirpath + "/" + filename;
			files.add(newDirpath);
			if (path.toFile().isDirectory()) {
				listFilesRecursively(files, newDirpath);
			}
		}
		paths.close();
	}
	
	public List<String> listFiles() throws Exception {
		return listFiles(this.directory.getAbsolutePath());
	}
	
	public void listFilesRecursively(List<String> files) throws Exception {
		listFilesRecursively(files, "/");
	}
	
	public boolean containsFile(String filepath) throws Exception {
		File file = new File(this.directory, filepath);
		return file.exists();
	}
	
	public long countAllFiles() throws Exception {
		return Files.walk(this.directory.toPath(),
			FileVisitOption.FOLLOW_LINKS)
				.filter(path -> ! path.toFile().isDirectory())
				.count();
	}
	
	public Date getDateOfMostRecentChange(PatternSets patternSets) throws Exception {
		
		long mostRecentlyModified = 0;
		boolean firstTime = true;
		// Iterate through all of the files.
		DirectoryStream<Path> paths = Files.newDirectoryStream(directory.toPath());
		for (Path path : paths) {
			long lastModified = path.toFile().lastModified();
			if (firstTime) {
				firstTime = false;
				mostRecentlyModified = lastModified;
			} else {
				if (lastModified > mostRecentlyModified) mostRecentlyModified = lastModified;
			}
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(mostRecentlyModified);
		return calendar.getTime();
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
