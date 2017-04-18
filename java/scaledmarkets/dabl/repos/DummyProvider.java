package scaledmarkets.dabl.repos;

import scaledmarkets.dabl.exec.Repo;
import scaledmarkets.dabl.exec.RepoProvider;
import scaledmarkets.dabl.exec.PatternSets;
import java.io.File;

public class DummyProvider implements RepoProvider {
	
	public static String RepoType = "dummy";
	
	public Repo getRepo(String scheme, String path, String userid, String password) throws Exception {
		return new DummyRepo(scheme, path, userid, password);
	}
	
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		....print the files that we would get.
	}
	
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		....print the files that we would put.
	}
		
	class DummyRepo extends Repo {
		
		DummyRepo(String scheme, String path, String userid, String password) {
			super(RepoType, scheme, path, userid, password);
		}
		
		protected RepoProvider getRepoProvider() { return DummyProvider.this; }
	}
}
