package scaledmarkets.dabl.exec;

import java.io.File;

public interface RepoProvider {
	RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception;
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	void putFiles(File dir, PatternSets patternSets) throws Exception;
}
