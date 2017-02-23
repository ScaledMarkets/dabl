# language: en

Feature: TestHelpers
	
	@done
	Scenario: Test getPrimaryNamespace
		When I call getPrimaryNamespace
		Then it returns the correct AOnamespace symbol
	
	@done
	Scenario: Test getNamespace
		When I call getNamespace with a start symbol
		Then it returns the AOnamespace symbol for that AST
	
	@done
	Scenario: Test getNamespaceFullName
		When I call getNamespaceFullName with a namespace argument
		Then it returns the fully qualified name of that namespace
	
	@done
	Scenario: Test getNamespaceElements()
		When I call getNamespaceElements
		Then it returns the POnamespaceElt elements of the namespace
	
	@done
	Scenario: Test getNamespaceElements(Start start)
		When I call getNamespaceElements with a start symbol
		Then it returns the POnamespaceElt elements for that AST
	
	@done
	Scenario: Test getImportedNamespaces()
		When I call getImportedNamespaces
		Then it returns the AImportOnamespaceElt elements for the primary namespace
	
	@done
	Scenario: Test getImportedNamespaces(Start start)
		When I call getImportedNamespaces with a start symbol
		Then it returns the AImportOnamespaceElt elements for the specified AST
	
	@done
	Scenario: Test getArtifactDeclarations()
		When I call getArtifactDeclarations
		Then it returns the AOartifactDeclarations for the namespace
	
	@done
	Scenario: Test getArtifactDeclarations(Start start)
		When I call getArtifactDeclarations with a start symbol
		Then it returns the AOartifactDeclarations for the specified AST
	
	@done
	Scenario: Test getRepoDeclarations()
		When I call getRepoDeclarations
		Then it returns the AOrepoDeclarations for the namespace
	
	@done
	Scenario: Test getRepoDeclarations(Start start)
		When I call getRepoDeclarations with a start symbol
		Then it returns the AOrepoDeclarations for the specified AST
	
	@done
	Scenario: Test getFilesDeclarations()
		When I call getFilesDeclarations
		Then it returns the AOfilesDeclarations for the namespace
	
	@done
	Scenario: Test getFilesDeclarations(Start start)
		When I call getFilesDeclarations with a start symbol
		Then it returns the AOfilesDeclarations for the specified AST
	
	@done
	Scenario: Test getFunctionDeclarations()
		When I call getFunctionDeclarations
		Then it returns the AOfunctionDeclarations for the namespace
	
	@done
	Scenario: Test getFunctionDeclarations(Start start)
		When I call getFunctionDeclarations with a start symbol
		Then it returns the AOfunctionDeclarations for the specified AST
	
	@done
	Scenario: Test getTaskDeclarations()
		When I call getTaskDeclarations
		Then it returns the AOtaskDeclarations for the namespace
	
	@done
	Scenario: Test getTaskDeclarations(Start start)
		When I call getTaskDeclarations with a start symbol
		Then it returns the AOtaskDeclarations for the specified AST
	
	@done
	Scenario: Test getPrimaryNamespaceSymbolEntry()
		When I call getPrimaryNamespaceSymbolEntry
		Then it returns the NameScopeEntry for the primary namespace
	
	@done
	Scenario: Test getNamespaceSymbolEntry(String namespaceName)
		When I call getNamespaceSymbolEntry
		Then it returns the NameScopeEntry for the specified namespace
	
	@done
	Scenario: Test getDeclaredEntry(NameScopeEntry namespaceEntry, String name)
		When I call getDeclaredEntry
		Then it returns the DeclaredEntry for the specified name, within the specified name scope
	
	@done
	Scenario: Test getDeclaredEntry(String name)
		When I call getDeclaredEntry
		Then it returns the DeclaredEntry for the specified name within the top level of the namespace
	
	@done
	Scenario: Test getDeclaration(String name)
		When I call getDeclaration
		Then it returns the declaring Node for the specified name at the top level of the namespace
	
	@done
	Scenario: Test getArtifactDeclaration(String artifactName)
		When I call getArtifactDeclaration
		Then it returns the artifact declaration with the specified name
