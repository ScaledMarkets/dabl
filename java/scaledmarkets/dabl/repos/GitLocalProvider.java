package scaledmarkets.dabl.repos;

import scaledmarkets.dabl.exec.RemoteRepo;
import scaledmarkets.dabl.exec.RepoProvider;
import scaledmarkets.dabl.exec.PatternSets;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;
import java.io.File;

public class GitLocalProvider implements RepoProvider {
	
	public static String RepoType = "gitlocal";
	
	public RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception {
		return new GitLocalRepo(scheme, path, project, userid, password);
	}
	
	// References:
	
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		/*
		// Synchronize the local repo/branch.
		....
		
		
		
		for () {
			
			
			
		}
		*/
	}
	
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		//....
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

	class GitLocalRepo extends RemoteRepo {
		
		GitLocalRepo(String scheme, String path, String project,
			String userid, String password) {
			super(RepoType, scheme, path, project, userid, password);
		}
		
		protected RepoProvider getRepoProvider() { return GitLocalProvider.this; }
	}
}
