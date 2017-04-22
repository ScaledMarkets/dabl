package scaledmarkets.dabl.exec;

import java.io.File;

public interface Repo {
	
	/**
	 * Retrieve the specified files from the repository.
	 */
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	
	/**
	 * Store ("push") the specified files to the specified repository.
	 */
	void putFiles(File dir, PatternSets patternSets) throws Exception;
}
