package scaledmarkets.dabl.exec;

import java.io.File;
import java.util.List;
import java.util.Date;

/**
 * A repo that maintains unified change history for each of a collection of files.
 */
public interface SnapshotRepo extends Repo {
	/**
	 * Add a tag (label) to the specified version of this Repo. The format of
	 * the version depends on the type of Repo. For example, a git repo version
	 * string might be a hash, or it might be an existing tag. See the implementation
	 * note for the Repo to find out the requirements for the version parameter.
	 */
	void addTag(String version, String tag) throws Exception;
}
