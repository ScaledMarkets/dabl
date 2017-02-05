package scaledmarkets.dabl.main;


public interface ImportHandler {

	public NameScope importNamespace(String path, CompilerState state);
}
