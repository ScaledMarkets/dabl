package scaledmarkets.dabl.analyzer;

import java.io.Reader;

/**
 * Parses and analyzes DABL source code. The source code is provided as input.
 */
public interface NamespaceProcessor {
	
	NameScope processNamespace(Reader reader, CompilerState state);
}
