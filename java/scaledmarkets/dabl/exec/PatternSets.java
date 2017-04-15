package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import java.util.List;
import java.util.LinkedList;

public class PatternSets implements Comparable<PatternSets> {
	
	public PatternSets(Repo repo, String project) {
		this.repo = repo;
		this.project = project;
	}
	
	private Repo repo;
	private String project;
	private List<String> includePatterns = new LinkedList<String>();
	private List<String> excludePatterns = new LinkedList<String>();
	
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
}
