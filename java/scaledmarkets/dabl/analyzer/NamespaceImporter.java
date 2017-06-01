package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public interface NamespaceImporter {
	
	NameScope importNamespace(Reader reader, CompilerState state);
}
