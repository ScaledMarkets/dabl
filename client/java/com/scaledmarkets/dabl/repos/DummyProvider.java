package com.scaledmarkets.dabl.repos;

import com.scaledmarkets.dabl.exec.RemoteRepo;
import com.scaledmarkets.dabl.exec.RepoProvider;
import com.scaledmarkets.dabl.exec.PatternSets;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Date;

/**
 * Used for testing. Does not actually interact with a repository: instead, the
 * actions that would have been performed are recorded, and can be retrieved
 * with the getPulledFiles and getPushedFiles methods.
 */
public class DummyProvider implements RepoProvider {
	
	public static String RepoType = "dummy";
	private List<String> pulledFiles = new LinkedList<String>();
	private List<String> pushedFiles = new LinkedList<String>();
	
	public RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception {
		return new DummyRepo(scheme, path, project, userid, password);
	}
	
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		pulledFiles.add(patternSets.toString());
	}
	
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		pushedFiles.add(patternSets.toString());
	}
		
	class DummyRepo extends RemoteRepo {
		
		DummyRepo(String scheme, String path, String project, String userid, String password) {
			super(RepoType, scheme, path, project, userid, password);
		}
		
		public RepoProvider getRepoProvider() { return DummyProvider.this; }
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

	public List<String> listFiles(RemoteRepo repo, String dirpath) throws Exception {
		return null; //....
	}
	
	public void listFilesRecursively(RemoteRepo repo, List<String> files, String dirpath) throws Exception {
		return; //....
	}
	
	public List<String> listFiles(RemoteRepo repo) throws Exception {
		return null; //....
	}
	
	public void listFilesRecursively(RemoteRepo repo, List<String> files) throws Exception {
		return; //....
	}
	
	public boolean containsFile(RemoteRepo repo, String filepath) throws Exception {
		return false; //....
	}
	
	public long countAllFiles(RemoteRepo repo) throws Exception {
		return 0; //....
	}

	public Date getDateOfMostRecentChange(PatternSets patternSets) throws Exception {
		return null;
	}
}
