package scaledmarkets.dabl.exec;

import java.io.File;
import java.util.List;

public interface RepoProvider {
	RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception;
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	void putFiles(File dir, PatternSets patternSets) throws Exception;


	List<String> listFiles(RemoteRepo repo, String dirpath) throws Exception;
	
	void listFilesRecursively(List<String> files, RemoteRepo repo, String dirpath) throws Exception;
	
	List<String> listFiles(RemoteRepo repo) throws Exception;
	
	void listFilesRecursively(List<String> files, RemoteRepo repo) throws Exception;
	
	boolean containsFile(RemoteRepo repo, String filepath) throws Exception;
	
	long countAllFiles(RemoteRepo repo) throws Exception;
}
