package scaledmarkets.dabl.test;

import scaledmarkets.dabl.main.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.Reader;
import java.io.StringReader;

/**
 * A table of DABL namespaces, indexed by namespace name. Intended for testing.
 */
public class InMemoryImportHandler implements ImportHandler {

	private Map<String, String> namespaces = new HashMap<String, String>();
	private Map<String, NameScope> scopeMap = new HashMap<String, NameScope>();
	
	public InMemoryImportHandler(Map<String, String> namespaces) throws Exception {
		this.namespaces = namespaces;
	}
	
	public NameScope importNamespace(String namespacePath, CompilerState state) {
		NameScope nameScope = scopeMap.get(namespacePath);
		if (nameScope == null) {
			
			Reader reader = new StringReader(namespaces.get(namespacePath));
			Dabl dabl = new Dabl(false, true, reader, new InMemoryImportHandler(new HashMap<String, String>()));
			
			nameScope = dabl.process(state);
			
			scopeMap.put(namespacePath, nameScope);
		}
		return nameScope;
	}
}
