package scaledmarkets.dabl.analyzer;


public interface ImportHandler {

	public NameScope importNamespace(String path, CompilerState state);
}
