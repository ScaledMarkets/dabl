package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import java.io.File;

public class LocalRepo implements Repo {
	
	public static LocalRepo createRepo(ALocalOartifactSet artifactSet) throws Exception {
		
		LocalRepo repo = new LocalRepo(artifactSet);
		....create directory
		
		return repo;
	}
	
	public ALocalOartifactSet getArtifactSet() { return artifactSet; }

	/**
	 * Retrieve the specified files from the repository.
	 */
	public void getFiles(PatternSets patternSets, File dir) throws Exception {
		
		....
	}
	
	/**
	 * Store ("push") the specified files to the specified repository.
	 */
	public void putFiles(File dir, PatternSets patternSets) throws Exception {
		
		....
	}

	private ALocalOartifactSet artifactSet;
	private File directory;
	
	protected LocalRepo(ALocalOartifactSet artifactSet) {
		this.artifactSet = artifactSet;
	}
}
