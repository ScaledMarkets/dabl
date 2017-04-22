package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.node.*;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.LinkedList;

/**
 * Perform an operation (specified by a derived class) on a collection of artifacts
 * which are specified via include/exclude pattern sets.
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
	protected void operateOnArtifacts(Set<Artifact> artifacts) throws Exception {
		
		PatternSetsMap patternSetsMap = new PatternSets.Map();
		
		for (Artifact artifact : artifacts) {
			POartifactSet artifactSet = artifact.getArtifactSet();
			AOidRef reposIdRef = (AOidRef)(artifactSet.getRepositoryId());
			
			Repo repo;
			if (artifactSet instanceof ALocalOartifactSet) {
				// Create a local repository, managed by DABL.
				
				// Find the NamedArtifactSet that owns the artifactSet.
				String outputName = getName((ALocalOartifactSet)artifactSet);
				repo = LocalRepo.createRepo(outputName, ALocalOartifactSet)artifactSet);
				
			} else if (artifactSet instanceof ARemoteOartifactSet) {
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
				repo = RemoteRepo.getRepo(repoType, scheme, path, userid, password);
				
			} else
				throw new RuntimeException(
					"Unexpected artifactSet type: " + artifactSet.getClass().getName());

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
	
	protected String getName(ALocalOartifactSet artifactSet) throws Exception {
		
		Node parent = artifactSet.parent();
		if (parent instanceof AOfilesDeclaration) {
			AOfilesDeclaration filesDecl = (AOfilesDeclaration)parent;
			TId id = filesDecl.getName();
			return id.getText();
		} else if (parent instanceof POnamedArtifactSet) {
			if (parent instanceof ANamedOnamedArtifactSet) {
				ANamedOnamedArtifactSet namedArtifactSet = (ANamedOnamedArtifactSet)parent;
				TId id = namedArtifactSet.getId();
				return id.getText();
			} else {
				throw new Exception("Artifact set must have a name");
			}
		} else throw new RuntimeException(
			"Unexpected Node kind: " + parent.getClass().getName());
	}
}
