package scaledmarkets.dabl.analyzer;

import java.io.Reader;

public class NamespaceImporter {
	
	public static NameScope importNamespace(Reader reader, CompilerState state) {

		Dabl dabl = new Dabl(false, false, reader, this);
		System.out.println("Processing " + path + "..."); // debug
		NameScope importedScope;
		try { importedScope = dabl.process(state); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		System.out.println("...done processing " + path); // debug
		
		return importedScope;
	}
}
