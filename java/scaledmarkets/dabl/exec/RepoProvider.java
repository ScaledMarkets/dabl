package scaledmarkets.dabl.exec;

import java.io.File;
import java.util.List;
import java.util.Date;

public interface RepoProvider {
	RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception;
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	void putFiles(File dir, PatternSets patternSets) throws Exception;


	List<String> listFiles(RemoteRepo repo, String dirpath) throws Exception;
	
	void listFilesRecursively(RemoteRepo repo, List<String> files, String dirpath) throws Exception;
	
	List<String> listFiles(RemoteRepo repo) throws Exception;
	
	void listFilesRecursively(RemoteRepo repo, List<String> files) throws Exception;
	
	boolean containsFile(RemoteRepo repo, String filepath) throws Exception;
	
	long countAllFiles(RemoteRepo repo) throws Exception;
	
	Date getDateOfMostRecentChange(PatternSets patternSets) throws Exception;
}
