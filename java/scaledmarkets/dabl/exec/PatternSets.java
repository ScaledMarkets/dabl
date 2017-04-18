package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;

/**
 * A collection of file patterns specifying files to include and exclude from
 * a repository project.
 */
public class PatternSets implements Comparable<PatternSets> {
	
	public PatternSets(Repo repo, String project) {
		this.repo = repo;
		this.project = project;
	}
	
	private Repo repo;
	private String project;
	private List<String> includePatterns = new LinkedList<String>();
	private List<String> excludePatterns = new LinkedList<String>();
	
	/**
	 * Create a key that uniquely identifies the repository/project combination.
	 * The key is not required to be externalizable.
	 */
	public static String getKey(Repo repo, String project) {
		return repo.hashCode() + ":" + project;
	}
	
	public String getKey() { return getKey(repo, project); }
	
	public Repo getRepo() { return this.repo; }
	
	public String getProject() { return this.project; }
	
	public int compareTo(PatternSets other) {
		return getKey().compareTo(other.getKey());
	}
	
	public List<String> getIncludePatterns() { return this.includePatterns; }
	
	public List<String> getExcludePatterns() { return this.excludePatterns; }
	
	/**
	 * A map of PatternSets, indexed by repo/project. This can be used to
	 * assemble a map of the include/exclude pattern sets for pulling or pushing
	 * files to/from the project.
	 */
	public static class Map extends HashMap<String, PatternSets> {
		
		/**
		 * Return the PatternSets for the specified repo and project. If it does
		 * not exist, create it.
		 */
		public PatternSets getPatternSets(Repo repo, String project) {
			String key = PatternSets.getKey(repo, project);
			PatternSets p = get(key);
			if (p == null) {
				p = new PatternSets(repo, project);
				put(key, p);
			}
			return p;
		}
	}
	
	public String toString() {
		String result = "";
		for (String pattern : includePatterns) {
			result = result + "+" + pattern + ";";
		}
		for (String pattern : excludePatterns) {
			result = result + "-" + pattern + ";";
		}
	}
}
