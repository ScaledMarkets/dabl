package com.scaledmarkets.dabl.test;

import com.scaledmarkets.dabl.client.*;
import com.scaledmarkets.dabl.analyzer.*;

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
	
	public InMemoryImportHandler(Map<String, String> namespaces) {
		this.namespaces = namespaces;
	}
	
	public NameScope processNamespace(String namespacePath, CompilerState state) {
		System.out.println("------------Importing namespace " + namespacePath); // debug
		NameScope nameScope = scopeMap.get(namespacePath);
		if (nameScope == null) try {
			
			Reader reader = new StringReader(namespaces.get(namespacePath));
			Dabl dabl = new Dabl(false, true, true, reader);
			
			System.out.println("Processing " + namespacePath + "..."); // debug
			nameScope = dabl.process(new DablAnalyzerFactory(state) {
				public ImportHandler createImportHandler() {
					return new InMemoryImportHandler(new HashMap<String, String>());
				}
			});
			System.out.println("...done processing " + namespacePath); // debug
			
			scopeMap.put(namespacePath, nameScope);
		}
		catch (Exception ex) { throw new RuntimeException(ex); }
		
		return nameScope;
	}
}
