package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import scaledmarkets.dabl.analyzer.*;
import scaledmarkets.dabl.helper.Helper;

import java.util.List;
import java.util.LinkedList;

/**
 * Convenience methods for traversing the final AST and Annotations that are produced
 * by the DABL compiler.
 */
public class ExecHelper extends Helper {
	
	public ExecHelper(CompilerState state) {
		super(state);
	}
	
	/**
	 * Convert the specified artifact set into an equivalent PatternSets. Construct
	 * a Repo object as needed (a PatternSets applies to a repo). If the Repo already
	 * exists in the RemoteRepo map, use that - and so the artifact set for that repo
	 * is merged into the existing artifact set for that repo. Similarly, if a
	 * PatternSets already exists for the repo/project combination that is specified
	 * by the artifact set, then use that PatternSets - and thus merge the pattern
	 * into that PatternSets object.
	 */
	public PatternSets convertArtifactSetToPatternSets(String namespaceName,
		String taskName, POartifactSet artifactSet,
		PatternSets.Map patternSetsMap, RemoteRepo.Map remoteRepoMap) throws Exception {
		
		Repo repo;
		List<POfilesetOperation> filesetOps;

		if (artifactSet instanceof ALocalOartifactSet) {
			// Find the NamedArtifactSet that owns the artifactSet.
			ALocalOartifactSet localArtifactSet = (ALocalOartifactSet)artifactSet;
			String outputName = ArtifactOperator.getName(localArtifactSet);

			// Create a local repository, managed by DABL.
			repo = LocalRepo.createRepo(namespaceName, taskName, outputName,
				localArtifactSet);
			
			filesetOps = localArtifactSet.getOfilesetOperation();
			
		} else if (artifactSet instanceof ARemoteOartifactSet) {
			ARemoteOartifactSet remoteArtifactSet = (ARemoteOartifactSet)artifactSet;
			AOidRef reposIdRef = (AOidRef)(remoteArtifactSet.getRepositoryId());
			
			// Use the Repo object to pull the files from the repo.
			String project = getStringLiteralValue(remoteArtifactSet.getProject());
			
			// Identify the repo declaration.
			AOrepoDeclaration repoDecl = getRepoDeclFromRepoRef(reposIdRef);
			String path = getStringLiteralValue(repoDecl.getPath());
			
			// Obtain the repo information.
			String scheme = getStringValueOpt(repoDecl.getScheme());
			String userid = getStringValueOpt(repoDecl.getUserid());
			String password = getStringValueOpt(repoDecl.getPassword());
			String repoType = getStringLiteralValue(repoDecl.getType());
			
			// Use the repo info to construct a Repo object.
			repo = remoteRepoMap.getRemoteRepo(repoType, scheme, path, project, userid, password);
			
			filesetOps = remoteArtifactSet.getOfilesetOperation();
			
		} else
			throw new RuntimeException(
				"Unexpected artifactSet type: " + artifactSet.getClass().getName());

		PatternSets patternSets = patternSetsMap.getPatternSets(repo);
			
		// Construct a set of include patterns and a set of exclude patterns.
		patternSets.assembleIncludesAndExcludes(this, filesetOps);

		return patternSets;
	}
	
	/**
	 * Convenience version of this method, for cases in which there is only one
	 * artifact set.
	 */
	public PatternSets convertArtifactSetToPatternSets(String namespaceName,
		String taskName,POartifactSet artifactSet)
	throws Exception {
		
		PatternSets.Map patternSetsMap = new PatternSets.Map();
		RemoteRepo.Map remoteRepoMap = new RemoteRepo.Map();
		
		return convertArtifactSetToPatternSets(namespaceName, taskName,
			artifactSet, patternSetsMap, remoteRepoMap);
	}
}
