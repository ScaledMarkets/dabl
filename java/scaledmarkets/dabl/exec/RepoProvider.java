package scaledmarkets.dabl.exec;

import java.io.File;

public interface RepoProvider {
	Repo getRepo(String scheme, String path, String userid, String password) throws Exception;
	void getFiles(PatternSets patternSets, File dir) throws Exception;
	void putFiles(File dir, PatternSets patternSets) throws Exception;
}
