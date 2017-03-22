# language: en

Feature: TestHelpers
	
	@done
	@helper
	Scenario: Test getPrimaryNamespace
		When I call getPrimaryNamespace
		Then it returns the correct AOnamespace symbol
	
	@done
	@helper
	Scenario: Test getNamespace
		When I call getNamespace with a start symbol
		Then it returns the AOnamespace symbol for that AST
	
	@done
	@helper
	Scenario: Test getNamespaceFullName
		When I call getNamespaceFullName with a namespace argument
		Then it returns the fully qualified name of that namespace
	
	@done
	@helper
	Scenario: Test getNamespaceElements
		When I call getNamespaceElements
		Then it returns the POnamespaceElt elements of the namespace
	
	@done
	@helper
	Scenario: Test getNamespaceElements with start
		When I call getNamespaceElements with a start symbol
		Then it returns the POnamespaceElt elements for that AST
	
	@done
	@helper
	Scenario: Test getImportedNamespaces
		When I call getImportedNamespaces
		Then it returns the AImportOnamespaceElt elements for the primary namespace
	
	@done
	@helper
	Scenario: Test getImportedNamespaces with start
		When I call getImportedNamespaces with a start symbol
		Then it returns the AImportOnamespaceElt elements for the specified AST
	
	@done
	@helper
	Scenario: Test getArtifactDeclarations
		When I call getArtifactDeclarations
		Then it returns the AOartifactDeclarations for the namespace
	
	@done
	@helper
	Scenario: Test getArtifactDeclarations with start
		When I call getArtifactDeclarations with a start symbol
		Then it returns the AOartifactDeclarations for the specified AST
	
	@done
	@helper
	Scenario: Test getRepoDeclarations
		When I call getRepoDeclarations
		Then it returns the AOrepoDeclarations for the namespace
	
	@done
	@helper
	Scenario: Test getRepoDeclarations with start
		When I call getRepoDeclarations with a start symbol
		Then it returns the AOrepoDeclarations for the specified AST
	
	@done
	@helper
	Scenario: Test getFilesDeclarations
		When I call getFilesDeclarations
		Then it returns the AOfilesDeclarations for the namespace
	
	@done
	@helper
	Scenario: Test getFilesDeclarations with start
		When I call getFilesDeclarations with a start symbol
		Then it returns the AOfilesDeclarations for the specified AST
	
	@done
	@helper
	Scenario: Test getFunctionDeclarations
		When I call getFunctionDeclarations
		Then it returns the AOfunctionDeclarations for the namespace
	
	@done
	@helper
	Scenario: Test getFunctionDeclarations with start
		When I call getFunctionDeclarations with a start symbol
		Then it returns the AOfunctionDeclarations for the specified AST
	
	@done
	@helper
	Scenario: Test getTaskDeclarations
		When I call getTaskDeclarations
		Then it returns the AOtaskDeclarations for the namespace
	
	@done
	@helper
	Scenario: Test getTaskDeclarations with start
		When I call getTaskDeclarations with a start symbol
		Then it returns the AOtaskDeclarations for the specified AST
	
	@done
	@helper
	Scenario: Test getPrimaryNamespaceSymbolEntry
		When I call getPrimaryNamespaceSymbolEntry
		Then it returns the NameScopeEntry for the primary namespace
	
	@done
	@helper
	Scenario: Test getNamespaceSymbolEntry
		When I call getNamespaceSymbolEntry
		Then it returns the NameScopeEntry for the specified namespace
	
	@done
	@helper
	Scenario: Test getDeclaredEntry
		When I call getDeclaredEntry
		Then it returns the DeclaredEntry for the specified name, within the specified name scope
	
	@done
	@helper
	Scenario: Test top level getDeclaredEntry
		When I call getDeclaredEntry
		Then it returns the DeclaredEntry for the specified name within the top level of the namespace
	
	@done
	@helper
	Scenario: Test top level getDeclaration
		When I call getDeclaration
		Then it returns the declaring Node for the specified name at the top level of the namespace
	
	@done
	@helper
	Scenario: Test getArtifactDeclaration
		When I call getArtifactDeclaration
		Then it returns the artifact declaration with the specified name
