package com.scaledmarkets.dabl.exec;

import java.io.File;
import java.util.List;
import java.util.Date;
import java.nio.file.Path;

/**
 * An artifact repository.
 */
public interface FileRepo extends Repo {
	/**
	 * Add a tag (label) to the specified file artifact in the Repo.
	 */
	void addTag(Path path, String tag) throws Exception;
}
