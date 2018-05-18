package scaledmarkets.dabl.analyzer;

/**
 * Finds the DABL source code for a specified namespace, and then parses and
 * analyzes that source (usually be delegating to a NamespaceProcessor).
 */
public interface ImportHandler {

	NameScope processNamespace(String namespacePath, CompilerState state);
}
