package scaledmarkets.dabl.repos;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;
import java.io.File;

public class GitLocalProvider implements RepoProvider {
	
	public Repo getRepo(String scheme, path, userid, password) throws Exception {
		
		return new GitLocalRepo(....);
	}
	
	class GitLocalRepo extends Repo {
		
		// References:
		
		public void getFiles(String project, PatternSets patternSets, File dir) throws Exception {
			
			// Synchronize the local repo/branch.
			....
			
			
			
			for () {
				
				
				
			}
			
		}
		
		public void putFiles(File dir, String project, PatternSets patternSets) throws Exception {
			
			....
		}
		
		protected RepoProvider getRepoProvider() { return GitLocalProvider.this; }
	}
}
