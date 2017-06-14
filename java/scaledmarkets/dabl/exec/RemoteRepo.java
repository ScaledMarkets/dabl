package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.util.Utilities;
import java.io.File;
import java.util.List;
import java.util.Date;

/**
 * A remote container for a set of files. By 'remote', we mean that the repo is
 * accessed via a URL.
 */
public abstract class RemoteRepo implements Repo {
	
	private String repoType, scheme, path, project, userid, password;
	
	public static RemoteRepo getRepo(String repoType, String scheme, String path,
		String project, String userid, String password) throws Exception {
		
		// Find the provider for the specified repo type.
		String repoProviderName = Utilities.getSetting("dabl.repo.providers." + repoType);
		if ((repoProviderName == null) || (repoProviderName.equals("")))
			throw new Exception("Provider for repo type " + repoType + " not found");
		Class repoProviderClass = Class.forName(repoProviderName);
		Object obj = repoProviderClass.newInstance();
		if (! (obj instanceof RepoProvider)) throw new Exception(
			"Class " + repoProviderClass.getName() + " is not a RepoProvider");
		RepoProvider repoProvider = (RepoProvider)obj;
		return repoProvider.getRepo(scheme, path, project, userid, password);
	}
	
	protected RemoteRepo(String repoType, String scheme, String path, String project,
		String userid, String password) {
	
		this.repoType = repoType;
		this.scheme = scheme;
		this.path = path;
		this.project = project;
		this.userid = userid;
		this.password = password;
	}

	public static class Map extends HashMap<String, RemoteRepo> {
		
		/**
		 * Return the RemoteRepo for the specified address. If it does
		 * not exist, create it.
		 */
		public RemoteRepo getRemoteRepo(String repoType, String scheme, String path,
				String project, String userid, String password) {
			
			String key = RemoteRepo.getKey(repoType, scheme, path, project);
			RemoteRepo r = get(key);
			if (r == null) {
				r = new getRepo(repoType, scheme, path, project, userid, password);
				put(key, r);
			}
			return r;
		}
	}
	
	static String getKey(String repoType, String scheme, String path, String project) {
		return repoType + " : " + scheme + " : " + path + " : " + project;
	}

	/**
	 * Retrieve the specified files from the specified project in the repository,
	 * and store them in 'dir'.
	 */
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		getRepoProvider().getFiles(patternSets, dir);
	}
	
	/**
	 * Store ("push") the specified files from 'dir' to the specified project of
	 * the repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		getRepoProvider().putFiles(dir, patternSets);
	}
	
	public String getRepoType() { return repoType; }
	
	public String getScheme() { return scheme; }
	
	public String getPath() { return path; }
	
	public String getProject() { return project; }
	
	public String getUserId() { return userid; }
	
	protected String getPassword() { return password; }
	
	public abstract RepoProvider getRepoProvider();

	public List<String> listFiles(String dirpath) throws Exception {
		return getRepoProvider().listFiles(this, dirpath);
	}
	
	public void listFilesRecursively(List<String> files, String dirpath) throws Exception {
		getRepoProvider().listFilesRecursively(this, files, dirpath);
	}
	
	public List<String> listFiles() throws Exception {
		return getRepoProvider().listFiles(this);
	}
	
	public void listFilesRecursively(List<String> files) throws Exception {
		getRepoProvider().listFilesRecursively(this, files);
	}
	
	public boolean containsFile(String filepath) throws Exception {
		return getRepoProvider().containsFile(this, filepath);
	}
	
	public long countAllFiles() throws Exception {
		return getRepoProvider().countAllFiles(this);
	}
	
	public Date getDateOfMostRecentChange(PatternSets patternSets) {
		return getRepoProvider().getDateOfMostRecentChange(patternSets);
	}
}
