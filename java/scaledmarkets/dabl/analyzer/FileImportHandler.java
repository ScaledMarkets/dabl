package scaledmarkets.dabl.analyzer;

import java.io.File;
import java.io.Reader;
import java.io.FileReader;

/**
 * Attempts to read the namespace from the file system, using the env
 * variable "DABL_PATH".
 */
public class FileImportHandler implements ImportHandler {

	private NamespaceProcessor namespaceProcessor;
	
	public FileImportHandler(NamespaceProcessor namespaceProcessor) {
		this.namespaceProcessor = namespaceProcessor;
	}
	
	public NameScope processNamespace(String path, CompilerState state) {
		
		System.out.println("------------Importing namespace " + path); // debug
		
		// Obtain value of DABL_PATH.
		String dablPath = System.getenv("DABL_PATH");
		if (dablPath == null) {
			throw new RuntimeException("Env variable DABL_PATH not set");
		}
		
		// Split the DABL path into its parts.
		String[] pathDirs = path.split(":");
		
		// Construct file path represented by the namespace path. I.e.,
		// convert periods to slashes.
		String relativeFilePath = path.replace('.', File.separatorChar);
		
		// Attempt to find a file with the specified path name.
		File namespaceFile = null;
		for (String dirStr : pathDirs) {
			File dir = new File(dirStr);
			if (! dir.isDirectory()) {
				throw new RuntimeException("File " + dir.toString() + " is not a directory");
			}
			
			File f = new File(dir, relativeFilePath);
			if (f.exists()) {  // found
				namespaceFile = f;
				break;
			}
		}
		if (namespaceFile == null) {
			throw new RuntimeException("Namespace " + path + " not found in the DABL_PATH");
		}
		
		// Recursively call the DABL compiler on the file.
		Reader reader;
		try {
			reader = new FileReader(namespaceFile);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return namespaceProcessor.processNamespace(reader, state);
	}
	
	public NamespaceProcessor getNamespaceProcessor() { return namespaceProcessor; }
}
