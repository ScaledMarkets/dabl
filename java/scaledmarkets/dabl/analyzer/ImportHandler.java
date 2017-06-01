package scaledmarkets.dabl.analyzer;


public interface ImportHandler {

	NameScope importNamespace(String path, CompilerState state);
	
	NamespaceImporter getNamespaceImporter();
}
