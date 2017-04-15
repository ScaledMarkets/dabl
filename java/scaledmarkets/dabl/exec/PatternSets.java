package scaledmarkets.dabl.exec;

import java.util.List;
import java.util.LinkedList;

public class PatternSets implements Comparable<PatternSets> {
	
	public PatternSets(String path, String project) {
		this.path = path;
		this.project = project;
	}
	
	private String path;
	private String project;
	private List<String> includePatterns = new LinkedList<String>();
	private List<String> excludePatterns = new LinkedList<String>();
	
	protected void assembleIncludesAndExcludes(List<POfilesetOperation>filesetOps) {
	
		for (POfilesetOperation op : filesetOps) {
			
			if (op instanceof AIncludeOfilesetOperation) {
				AIncludeOfilesetOperation includeOp = (AIncludeOfilesetOperation)op;
				POstringLiteral lit = includeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				includePatterns.add(pattern);
			} else if (op instanceof AExcludeOfilesetOperation) {
				AExcludeOfilesetOperation excludeOp = (AExcludeOfilesetOperation)op;
				POstringLiteral lit = excludeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				excludePatterns.add(pattern);
			} else throw new RuntimeException(
				"Unexpected POfilesetOperation type: " + op.getClass().getName());
		}
	}
	
	public static String getKey(String path, String project) {
		return path + ":" + project;
	}
	
	public String getKey() { return getKey(path, project); }
	
	public int compareTo(PatternSets other) {
		return getKey().compareTo(other.getKey());
	}
}
