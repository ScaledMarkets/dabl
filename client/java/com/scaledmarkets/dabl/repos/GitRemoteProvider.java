package com.scaledmarkets.dabl.repos;

import com.scaledmarkets.dabl.exec.RemoteRepo;
import com.scaledmarkets.dabl.exec.RepoProvider;
import com.scaledmarkets.dabl.exec.PatternSets;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.*;
import java.io.File;
import java.util.List;
import java.util.Date;

public class GitRemoteProvider implements RepoProvider {
	
	public static String RepoType = "gitremote";

	public RemoteRepo getRepo(String scheme, String path, String project,
		String userid, String password) throws Exception {
		return new GitRemoteRepo(scheme, path, project, userid, password);
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
	
	public List<String> listFiles(RemoteRepo repo, String dirpath) throws Exception {
		return null; //....
	}
	
	public void listFilesRecursively(RemoteRepo repo, List<String> files, String dirpath) throws Exception {
		//....
	}
	
	public List<String> listFiles(RemoteRepo repo) throws Exception {
		return null; //....
	}
	
	public void listFilesRecursively(RemoteRepo repo, List<String> files) throws Exception {
		//....
	}
	
	public boolean containsFile(RemoteRepo repo, String filepath) throws Exception {
		return false; //....
	}
	
	public long countAllFiles(RemoteRepo repo) throws Exception {
		return 0; //....
	}

	public Date getDateOfMostRecentChange(PatternSets patternSets) throws Exception {
		return null; //....
	}

	class GitRemoteRepo extends RemoteRepo {
		
		GitRemoteRepo(String scheme, String path, String project,
			String userid, String password) {
			super(RepoType, scheme, path, project, userid, password);
		}
		
		public RepoProvider getRepoProvider() { return GitRemoteProvider.this; }
	}
}
