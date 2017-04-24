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
	
	public RemoteRepo getRepo(String scheme, String path, String userid, String password) throws Exception {
		return new GitLocalRepo(scheme, path, userid, password);
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
		
	class GitLocalRepo extends RemoteRepo {
		
		GitLocalRepo(String scheme, String path, String userid, String password) {
			super(RepoType, scheme, path, userid, password);
		}
		
		protected RepoProvider getRepoProvider() { return GitLocalProvider.this; }
	}
}
