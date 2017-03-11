package scaledmarkets.dabl.analysis;


public interface ImportHandler {

	public NameScope importNamespace(String path, CompilerState state);
}
