package scaledmarkets.dabl.repos;

import scaledmarkets.dabl.exec.Repo;
import scaledmarkets.dabl.exec.RepoProvider;
import scaledmarkets.dabl.exec.PatternSets;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;
import java.io.File;

public class GitRemoteProvider implements RepoProvider {
	
	public static String RepoType = "gitremote";

	public Repo getRepo(String scheme, String path, String userid, String password) throws Exception {
		return new GitRemoteRepo(scheme, path, userid, password);
	}
	
	// References:
	// https://developer.github.com/v3/repos/
	// https://developer.github.com/v3/repos/contents/
	
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		/*
		// Get list of files in the repo project/branch.
		....
		
		// Retrieve the files that match the include/exclude patterns.
		for (....each file in the project/branch) {
			
			if (....file matches the include patterns) {
				if (....file does not match the exclude patterns) {
					
					....Add the file to the list of files to retrieve.
				}
			}
		}
		
		// Prepare a REST client.
		ClientBuilder clientBuilder = ClientBuilder.newBuilder().withConfig(clientConfig);
		Client client = clientBuilder.build();
		
		// Use the REST client to retrieve the files in the list.
		for (....each file in the retrieve list) {
			....client.target(uri)
		}
		
		*/
		
		
	}
	
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		//....
	}
		
	class GitRemoteRepo extends Repo {
		
		GitRemoteRepo(String scheme, String path, String userid, String password) {
			super(RepoType, scheme, path, userid, password);
		}
		
		protected RepoProvider getRepoProvider() { return GitRemoteProvider.this; }
	}
}
