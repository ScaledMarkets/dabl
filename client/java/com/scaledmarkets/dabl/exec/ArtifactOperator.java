package com.scaledmarkets.dabl.exec;

import com.scaledmarkets.dabl.helper.Helper;
import com.scaledmarkets.dabl.node.*;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.LinkedList;

/**
 * Perform an operation (specified by a derived class) on a collection of artifacts
 * which are specified via include/exclude pattern sets. The sets of artifacts
 * are potentially overlapping.
 */
public abstract class ArtifactOperator {
	
	protected ExecHelper helper;
	
	public ArtifactOperator(ExecHelper helper) { this.helper = helper; }

	/**
	 * The operation to perform on the artifacts, once the includes and exclude
	 * patterns have been assembled.
	 */
	protected abstract void operation(PatternSets patternSets) throws Exception;
	
	/**
	 * Perform the specified operation on the specified set of artifacts, using
	 * the specified directory as a local work directory.
	 */
	public void operateOnArtifacts(String namespaceName, String taskName,
		Set<Artifact> artifacts) throws Exception {
		
		PatternSets.Map patternSetsMap = new PatternSets.Map();
		RemoteRepo.Map remoteRepoMap = new RemoteRepo.Map();
		
		for (Artifact artifact : artifacts) {
			POartifactSet artifactSet = artifact.getArtifactSet();
			
			PatternSets patternSets = 
				this.helper.convertArtifactSetToPatternSets(namespaceName,
					taskName,artifactSet, patternSetsMap, remoteRepoMap);
		}
		
		for (PatternSets patternSets : patternSetsMap.values()) {
			this.operation(patternSets);
		}
	}

	public static String getName(ALocalOartifactSet artifactSet) throws Exception {
		
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
