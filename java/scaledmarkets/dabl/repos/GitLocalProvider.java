package scaledmarkets.dabl.repos;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;

public class GitLocalProvider implements RepoProvider {
	
	public Repo getRepo(String scheme, path, userid, password) throws Exception {
		
		return new GitLocalRepo(....);
	}
	
	class GitLocalRepo extends Repo {
		
		// References:
		
		public void getFiles(String project, List<String> includePatterns,
			List<String> excludePatterns) throws Exception {
			
			for () {
				
				
				
			}
			
		}
		
		public void putFiles(String project, List<String> includePatterns,
			List<String> excludePatterns) throws Exception {
			
			....
		}
		
		protected RepoProvider getRepoProvider() { return GitLocalProvider.this; }
	}
}
