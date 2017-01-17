package scaledmarkets.dabl.main;

/**
 * Attempts to read the namespace from the file system, using the env
 * variable "DABL_PATH".
 */
public interface DefaultImportHandler implements ImportHandler {

	public NameScope importNamespace(String path) {
		....
	}
}
