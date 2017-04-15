package scaledmarkets.dabl.repos;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;

public class GitRemoteProvider implements RepoProvider {
	
	public Repo getRepo(String scheme, path, userid, password) throws Exception {
		
		return new GitRemoteRepo(....);
	}
	
	class GitRemoteRepo extends Repo {
		
		// References:
		// https://developer.github.com/v3/repos/
		// https://developer.github.com/v3/repos/contents/
		
		public void getFiles(String project, List<String> includePatterns,
			List<String> excludePatterns) throws Exception {
			
			....
			ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);
			
			Client client = clientBuilder.build();
			
			....client.target(uri)
		}
		
		public void putFiles(String project, List<String> includePatterns,
			List<String> excludePatterns) throws Exception {
			
			....
		}
		
		protected RepoProvider getRepoProvider() { return GitRemoteProvider.this; }
	}
}
