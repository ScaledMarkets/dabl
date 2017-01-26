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

	private Map<String, NameScope> scopeMap = new HashMap<String, NameScope>();
	
	
	public InMemoryImportHandler(String[] namespaces) throws Exception {
		
		for (String namespace : namespaces) {
			
			Reader reader = new StringReader(namespace);
			Dabl dabl = new Dabl(false, true, reader, new InMemoryImportHandler(new String[] {}));
			
			CompilerState state = dabl.process();
			
			NameScope globalScope = state.globalScope;
			SymbolTable symbolTable = globalScope.getSymbolTable();
			List<SymbolTable> childTables = symbolTable.getChildren();  // should only be one
			if (childTables.size() == 0) throw new RuntimeException(
				"No namespaces found");
			if (childTables.size() > 1) throw new RuntimeException(
				"Multiple namespaces defined in one file");
			for (SymbolTable childTable : childTables) {
				String name = childTable.getName();
				NameScope nameScope = childTable.getScope();
				scopeMap.put(name, nameScope);
			}
		}
	}
	
	public NameScope importNamespace(String namespacePath) {
		return scopeMap.get(namespacePath);
	}
}
