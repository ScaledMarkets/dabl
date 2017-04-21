package scaledmarkets.dabl.repos;

import scaledmarkets.dabl.exec.Repo;
import scaledmarkets.dabl.exec.RepoProvider;
import scaledmarkets.dabl.exec.PatternSets;
import java.io.File;
import java.util.List;
import java.util.LinkedList;

/**
 * Used for testing. Does not actually interact with a repository: instead, the
 * actions that would have been performed are recorded, and can be retrieved
 * with the getPulledFiles and getPushedFiles methods.
 */
public class DummyProvider implements RepoProvider {
	
	public static String RepoType = "dummy";
	private List<String> pulledFiles = new LinkedList<String>();
	private List<String> pushedFiles = new LinkedList<String>();
	
	public Repo getRepo(String scheme, String path, String userid, String password) throws Exception {
		return new DummyRepo(scheme, path, userid, password);
	}
	
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		pulledFiles.add(patternSets.toString());
	}
	
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		pushedFiles.add(patternSets.toString());
	}
		
	class DummyRepo extends Repo {
		
		DummyRepo(String scheme, String path, String userid, String password) {
			super(RepoType, scheme, path, userid, password);
		}
		
		protected RepoProvider getRepoProvider() { return DummyProvider.this; }
	}
	
	public List<String> getPulledFiles() {
		return pulledFiles;
	}
	
	public List<String> getPushedFiles() {
		return pushedFiles;
	}
	
	/**
	 * Clear this object's state, so that it can be used again.
	 */
	public void reset() {
		this.pulledFiles.clear();
		this.pushedFiles.clear();
	}
}