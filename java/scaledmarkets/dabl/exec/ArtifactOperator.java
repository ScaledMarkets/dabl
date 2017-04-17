package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.node.*;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.LinkedList;

/**
 * Perform an operation (specified by a derived class) on a collection of artifacts
 * which are specified via pattern sets.
 */
public abstract class ArtifactOperator {
	
	protected Helper helper;
	
	public ArtifactOperator(Helper helper) { this.helper = helper; }

	/**
	 * The operation to perform on the artifacts, once the includes and exclude
	 * patterns have been assembled.
	 */
	protected abstract void operation(PatternSets patternSets) throws Exception;
	
	/**
	 * Perform the specified operation on the specified set of artifacts, using
	 * the specified directory as a local work directory.
	 */
	protected void operateOnArtifacts(Set<Artifact> artifacts, File dir) throws Exception {
		
		PatternSetsMap patternSetsMap = new PatternSets.Map();
		
		for (Artifact artifact : artifacts) {
			AOartifactSet artifactSet = artifact.getArtifactSet();
			AOidRef reposIdRef = (AOidRef)(artifactSet.getRepositoryId());
			
			// Use the Repo object to pull the files from the repo.
			String project = this.helper.getStringLiteralValue(artifactSet.getProject());
			
			// Identify the repo declaration.
			AOrepoDeclaration repoDecl = this.helper.getRepoDeclFromRepoRef(reposIdRef);
			String path = this.helper.getStringLiteralValue(repoDecl.getPath());
			
			// Obtain the repo information.
			String scheme = this.helper.getStringValueOpt(repoDecl.getScheme());
			String userid = this.helper.getStringValueOpt(repoDecl.getUserid());
			String password = this.helper.getStringValueOpt(repoDecl.getPassword());
			String repoType = this.helper.getStringLiteralValue(repoDecl.getType());
			
			// Use the repo info to construct a Repo object.
			Repo repo = Repo.getRepo(repoType, scheme, path, userid, password);

			PatternSets patternSets = patternSetsMap.getPatternSets(repo, project);
			
			// Construct a set of include patterns and a set of exclude patterns.
			LinkedList<POfilesetOperation> filesetOps = artifactSet.getOfilesetOperation();
			assembleIncludesAndExcludes(patternSets, filesetOps);
		}
		
		for (PatternSets patternSets : patternSetsMap.values()) {
			this.operation(patternSets);
		}
	}

	/**
	 * Add all of the include/exclude fileset operations to the include/exclude
	 * lists of the PatternSets.
	 */
	protected void assembleIncludesAndExcludes(PatternSets patternSets,
		List<POfilesetOperation> filesetOps) throws Exception {
	
		for (POfilesetOperation op : filesetOps) {
			
			if (op instanceof AIncludeOfilesetOperation) {
				AIncludeOfilesetOperation includeOp = (AIncludeOfilesetOperation)op;
				POstringLiteral lit = includeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				patternSets.getIncludePatterns().add(pattern);
			} else if (op instanceof AExcludeOfilesetOperation) {
				AExcludeOfilesetOperation excludeOp = (AExcludeOfilesetOperation)op;
				POstringLiteral lit = excludeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				patternSets.getExcludePatterns().add(pattern);
			} else throw new RuntimeException(
				"Unexpected POfilesetOperation type: " + op.getClass().getName());
		}
	}
}
