package scaledmarkets.dabl.exec;

import java.io.File;
import java.util.List;

/**
 * A container for a set of files, such as a repository project, or a directory.
 */
public interface Repo {
	
	/**
	 * Copy the specified files from the repository into the specified directory.
	 */
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	
	/**
	 * Store ("push") the specified files from 'dir' to the specified repository.
	 * 'dir' is treated as the filesystem root, for the purpose of pattern set matching.
	 */
	void putFiles(File dir, PatternSets patternSets) throws Exception;
	
	/**
	 * List the files that exist in the specified directory in this Repo.
	 * The directory may be a path of directories.
	 * The file path separator is a forward slash, and the root ("/") is the top
	 * of the Repo.
	 */
	List<String> listFiles(String dirpath) throws Exception;
	
	/**
	 * List the files that exist in the specified directory and all child
	 * directories in this repo.
	 */
	List<String> listFilesRecursively(String dirpath) throws Exception;
	
	/**
	 * List the files that exist at the top level in this repo.
	 */
	List<String> listFiles() throws Exception;
	
	/**
	 * List all of the files in this repo - including those in subdirectories.
	 */
	List<String> listFilesRecursively() throws Exception;
	
	/**
	 * Return true if this Repo contains the specified file. The file is fully
	 * specified by its path within the Repo. The file may be a regular file
	 * or a directory.
	 */
	boolean containsFile(String filepath) throws Exception;
	
	/**
	 * Return the total number of non-directory files in all directories of this Repo.
	 */
	int countFiles() throws Exception;
}
