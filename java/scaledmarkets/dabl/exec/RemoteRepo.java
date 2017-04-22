package scaledmarkets.dabl.exec;

import java.io.File;

public abstract class RemoteRepo implements Repo {
	
	private String repoType, scheme, path, userid, password;
	
	public static RemoteRepo getRepo(String repoType, String scheme, String path,
		String userid, String password) throws Exception {
		
		// Find the provider for the specified repo type.
		String repoProviderName = Utilities.getSetting("repo.providers." + repoType);
		if (repoProviderName == null) throw new Exception(
			"Provider for repo type " + repoType + " not found");
		Class repoProviderClass = Class.forName(repoProviderName);
		Object obj = repoProviderClass.newInstance();
		if (! (obj instanceof RepoProvider)) throw new Exception(
			"Class " + repoProviderClass.getName() + " is not a RepoProvider");
		RepoProvider repoProvider = (RepoProvider)obj;
		return repoProvider.getRepo(scheme, path, userid, password);
	}
	
	protected RemoteRepo(String repoType, String scheme, String path,
		String userid, String password) {
	
		this.repoType = repoType;
		this.scheme = scheme;
		this.path = path;
		this.userid = userid;
		this.password = password;
	}

	/**
	 * Retrieve the specified files from the specified project in the repository.
	 */
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		getRepoProvider().getFiles(patternSets, dir);
	}
	
	/**
	 * Store ("push") the specified files to the specified project of the repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		getRepoProvider().putFiles(dir, patternSets);
	}
	
	public String getRepoType() { return repoType; }
	
	public String getScheme() { return scheme; }
	
	public String getPath() { return path; }
	
	public String getUserId() { return userid; }
	
	protected String getPassword() { return password; }
	
	protected abstract RepoProvider getRepoProvider();
}
