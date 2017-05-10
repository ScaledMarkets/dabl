package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.util.Utilities;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.DirectoryStream;

/**
 * A collection of file patterns specifying files to include and exclude from
 * a repository project or directory.
 */
public class PatternSets implements Comparable<PatternSets> {
	
	public PatternSets(Repo repo) {
		this.repo = repo;
	}
	
	private Repo repo;
	private List<String> includePatterns = new LinkedList<String>();
	private List<String> excludePatterns = new LinkedList<String>();
	
	/**
	 * Create a key that uniquely identifies the repo.
	 * The key is not required to be externalizable.
	 */
	public static String getKey(Repo repo) {
		return String.valueOf(repo.hashCode());
	}
	
	public String getKey() { return getKey(repo); }
	
	public Repo getRepo() { return this.repo; }
	
	public int compareTo(PatternSets other) {
		return getKey().compareTo(other.getKey());
	}
	
	/**
	 * A map of PatternSets, indexed by repo. This can be used to
	 * assemble a map of the include/exclude pattern sets for pulling or pushing
	 * files to/from the repo.
	 */
	public static class Map extends HashMap<String, PatternSets> {
		
		/**
		 * Return the PatternSets for the specified repo. If it does
		 * not exist, create it.
		 */
		public PatternSets getPatternSets(Repo repo) {
			String key = PatternSets.getKey(repo);
			PatternSets p = get(key);
			if (p == null) {
				p = new PatternSets(repo);
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
		void op(File root, String pathRelativeToRoot) throws Exception;
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
	public void operateOnFiles(File patternRoot, File curDir, FileOperator fileOperator) throws Exception {
		
		// Verify that curDir is within the tree of patternRoot.
		patternRoot.toPath().relativize(curDir.toPath());
		
		System.out.println("For dir " + patternRoot.toString());  // debug
		
		Set<File> visited = new HashSet<File>();
		
		FileSystem fileSystem = FileSystems.getDefault();
		
		for (String pi : this.includePatterns) {
			
			System.out.println("\tConsidering pattern " + pi);  // debug
			
			// Get the files F of curDir.
			DirectoryStream<Path> F = Files.newDirectoryStream(curDir.toPath());
			
			
			// debug
			System.out.println("curDir=" + curDir.toString());
			System.out.println("curDir path = " + curDir.toPath().toString());
			System.out.println("curDir contains " + curDir.list().length + " elements");
			// end debug
			
			
			
			
			match: for (Path f : F) {
				
				System.out.println("\t\tConsidering path " + f.toString());  // debug
			
				// Check if we have already operated on the file.
				File matchingFile = new File(curDir, f.getFileName().toString());
				if (visited.contains(matchingFile)) // the file has been visited
					continue match; // then skip it.
				
				visited.add(matchingFile);
				
				// Translate f to patternRoot, as relative path g.
				// I.e., remove patternRoot from front of f
				Path g = patternRoot.toPath().relativize(f);
				
				// If g does not match pi, continue to next file of F.
				PathMatcher includeMatcher = fileSystem.getPathMatcher("glob:" + pi);
				if (! includeMatcher.matches(g)) { // skip the file.
					
					System.out.println("\t\tpath does not match include pattern");  // debug
					continue match;
				}
				
				for (String pe : this.excludePatterns) {
					
					PathMatcher excludeMatcher = fileSystem.getPathMatcher("glob:" + pe);
					
					if (excludeMatcher.matches(g)) { // skip the file.
						System.out.println("\t\tpath matches exclude pattern");  // debug
						continue match;
					}
				}
				
				System.out.println("\t\toperating on " + patternRoot + ", " + g);  // debug
				fileOperator.op(patternRoot, g.toString());
				
				if (matchingFile.isDirectory()) {
					// Call recursively for matchingFile.
					operateOnFiles(patternRoot, matchingFile, fileOperator);
				}
			}
		}
	}
	
	public List<String> getIncludePatterns() { return includePatterns; }
	
	public List<String> getExcludePatterns() { return excludePatterns; }
	
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
		return result;
	}
}
