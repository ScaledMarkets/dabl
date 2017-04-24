package scaledmarkets.dabl.exec;

import java.io.File;

/**
 * A container for a set of files.
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
}
