package scaledmarkets.dabl.analyzer;

/**
 * Find the DABL source code for a specified namespace, and then parses and
 * analyzes that source (usually be delegating to a NamespaceAnalyzer).
 */
public interface ImportHandler {

	NameScope importNamespace(String namespacePath, CompilerState state);
	
	NamespaceAnalyzer getNamespaceImporter();
}
