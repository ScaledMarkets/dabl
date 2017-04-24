package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.FileSystem;
import java.nio.file.PathMatcher;
import java.nio.file.DirectoryStream;

/**
 * A collection of file patterns specifying files to include and exclude from
 * a repository project or directory.
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
	
	/**
	 * Add all of the include/exclude fileset operations to the include/exclude
	 * lists of this PatternSets.
	 */
	public void assembleIncludesAndExcludes(Helper helper,
		List<POfilesetOperation> filesetOps) throws Exception {
	
		for (POfilesetOperation op : filesetOps) {
			
			if (op instanceof AIncludeOfilesetOperation) {
				AIncludeOfilesetOperation includeOp = (AIncludeOfilesetOperation)op;
				POstringLiteral lit = includeOp.getOstringLiteral();
				String pattern = helper.getStringLiteralValue(lit);
				this.includePatterns.add(pattern);
			} else if (op instanceof AExcludeOfilesetOperation) {
				AExcludeOfilesetOperation excludeOp = (AExcludeOfilesetOperation)op;
				POstringLiteral lit = excludeOp.getOstringLiteral();
				String pattern = helper.getStringLiteralValue(lit);
				this.excludePatterns.add(pattern);
			} else throw new RuntimeException(
				"Unexpected POfilesetOperation type: " + op.getClass().getName());
		}
	}
	
	public interface FileOperator {
		op(File root, String pathRelativeToRoot) throws Exception;
	}
	
	/**
	 * Perform an operation on the files specified by this PatternSets.
	 * @param patternRoot - The directory in which the files are rooted.
	 * @param fileOperator - Operation to perform on each matching file.
	 */
	public void operateOnFiles(File patternRoot, FileOperator fileOperator) throws Exception {
		operateOnFiles(patternRoot, patternRoot, fileOperator);
	}
	
	/**
	 * Perform an operation on the files specified by this PatternSets. Recursive.
	 * @param patternRoot - The directory in which the files are rooted.
	 * @param curDir - Directory from which search begins.
	 * @param fileOperator - Operation to perform on each matching file.
	 */
	protected void operateOnFiles(File patternRoot, File curDir, FileOperator fileOperator) throws Exception {
		
		// Verify that curDir is within the tree of patternRoot.
		patternRoot.toPath().relativize(curDir.toPath());
		
		Set<File> visited = new HashSet<File>();
		
		FileSystem fileSystem = FileSystem.getDefault();
		
		for (String pi : this.includePatterns) {
			
			// Get the files F of curDir matching qi.
			DirectoryStream<Path> F = Files.newDirectoryStream(curDir, qi);
			
			match: for (Path f : F) {
			
				// 
				File matchingFile = new File(curDir, f.getFileName());
				if (visited.contains(matchingFile)) // the file has been visited
					continue match; // then skip it.
				
				visited.add(matchingFile);
				
				// Translate f to patternRoot, as relative path g.
				// I.e., remove patternRoot from front of f
				Path g = patternRoot.toPath().relativize(f.toPath());
				
				// If g does not match pi, continue to next file of F.
				PathMatcher includeMatcher = fileSystem.getPathMatcher(
						"glob:" + pi);
				if (! includeMatcher.matches(g)) {
					continue match;
				}
				
				for (String pe : this.excludePatterns) {
					
					PathMatcher excludeMatcher = fileSystem.getPathMatcher(
						"glob:" + pe);
					
					if (excludeMatcher.matches(g)) {
						continue match; // skip the file.
					}
				}
				
				if (f.isDirectory()) {
					// Call recursively for matchingFile.
					operateOnFiles(patternRoot, f, fileOperator);
				} else {
					fileOperator.op(patternRoot, g);
				}
			}
		}
	}
	
	/**
	 * Provide a value that can be used to map pattern sets.
	 */
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
