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
	 * Retrieve the specified files from the repository and perform the specified
	 * operation on them.
	 */
	public void operateOnFiles(File patternRoot, File curDir, FileOperator fileOperator) throws Exception {
		
		Set<File> visited = new HashSet<File>();
		
		FileSystem fileSystem = FileSystem.getDefault();
		
		incl: for (String includePattern : this.includePatterns) {
			
			// Make the include pattern relative to curDir.
			// It is assumed that curDir is 0 or more levels below patternRoot.
			// Thus, we merely remove directory names until we reach curDir.
			String[] parts = includePattern.split(File.pathSeparator);
			
			String patternRelativeToCurDir = ....
			
			// Create a stream of immediate members of curDir.
			DirectoryStream<Path> stream =
				Files.newDirectoryStream(curDir, patternRelativeToCurDir);
			
			match: for (Path matchingPath : stream) {
			
				File matchingFile = new File(curDir, matchingPath.getFileName());
				if (visited.contains(matchingFile)) // the file has been visited
					continue match; // then skip it.
				
				visited.add(matchingFile);
				
				// Compute path relative to pattern room.
				Path pathRelativeToPatternRoot = ....
				
				excl: for (String excludePattern : this.excludePatterns) {
					
					PathMatcher excludeMatcher = fileSystem.getPathMatcher(
						"glob:" + excludePattern);
					
					if (excludeMatcher.matches(pathRelativeToPatternRoot)) {
						continue match; // skip the file.
					}
				}
				
				if (matchingFile.isDirectory()) {
					// Call recursively for matchingFile.
					operateOnFiles(patternRoot, matchingFile, fileOperator);
				} else {
					fileOperator.op(patternRoot, pathRelativeToPatternRoot);
				}
			}
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
