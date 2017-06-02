package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public interface NamespaceAnalyzer {
	
	NameScope importNamespace(Reader reader, CompilerState state);
}
