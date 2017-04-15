package scaledmarkets.dabl.exec;

import java.util.List;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

public class Repo {
	
	private String repoType, scheme, path, userid, password;
	
	public static String PropertyFileName = "dabl.properties";
	
	public static Repo getRepo(String repoType, String scheme, String path,
		String userid, String password) throws Exception {
		
		// Find the provider for the specified repo type.
		Properties properties = new Properties();
		String propertyFileName = PropertyFileName;
		ClassLoader classLoader = Repo.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(propertyFileName):
		if (inputStream == null) throw new FileNotFoundException(
			"property file " + PropertyFileName + " not found");
		String repoProviderName = properties.getProperty("repo.providers." + repoType);
		if (repoProviderName == null) throw new Exception(
			"Provider for repo type " + repoType + " not found");
		Class repoProviderClass = classLoader.loadClass(repoProviderName, true);
		Object obj = repoProviderClass.newInstance();
		if (! (obj instanceof RepoProvider)) throw new Exception(
			"Class " + repoProviderClass.getName() + " is not a RepoProvider");
		RepoProvider repoProvider = (RepoProvider)obj;
		return repoProvider.getRepo(scheme, path, userid, password);
	}
	
	protected Repo(String repoType, String scheme, String path,
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
	public void getFiles(String project, PatternSets patternSets, File dir) throws Exception {
		
		getRepoProvider().getFiles(project, patternSets, dir);
	}
	
	/**
	 * Store ("push") the specified files to the specified project of the repository.
	 */
	public void putFiles(File dir, String project, PatternSets patternSets) throws Exception {
		
		getRepoProvider().putFiles(dir, project, patternSets);
	}
	
	public String getRepoType() { return repoType; }
	
	public String getScheme() { return scheme; }
	
	public String getPath() { return path; }
	
	public String getUserId() { return userid; }
	
	protected String getPassword() { return password; }
	
	protected abstract RepoProvider getRepoProvider();
}