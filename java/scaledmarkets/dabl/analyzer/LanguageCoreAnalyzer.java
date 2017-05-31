package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import scaledmarkets.dabl.task.DablStandard;

import java.util.List;
import java.util.LinkedList;

/**
 * Language analysis that is shared among the LanguageAnalyzer and the TaskProgramAnalyzer.
 */
public class LanguageCoreAnalyzer extends DablBaseAdapter {

	protected ImportHandler importHandler;
	protected NameScopeEntry enclosingScopeEntry;
	protected NameScope namespaceNamescope;
	
	public LanguageCoreAnalyzer(CompilerState state, ImportHandler importHandler) {
		super(state);
		this.importHandler = importHandler;
		
		if (state.globalScope == null) {
			state.setGlobalScope(pushNameScope(new NameScope("Global", null, null)));
		}
	}

	public ImportHandler getImportHandler() { return importHandler; }

	public NameScopeEntry getEnclosingScopeEntry() { return enclosingScopeEntry; }
	
	public NameScope getNamespaceNamescope() { return namespaceNamescope; }
	
	
	/* Only onamespace and otask_declaration define name scopes. */
	
	public void inAOnamespace(AOnamespace node) {
		LinkedList<TId> path = node.getPath();
		String name = Utilities.createNameFromPath(path);
		
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = createNameScope(name, node);  // pushes name scope
											// and annotates the namespace Node.
		NameScopeEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		this.enclosingScopeEntry = entry;
		this.namespaceNamescope = newScope;
	}
	
	public void outAOnamespace(AOnamespace node) {
				
		....Check if there are unresolved symbols
		
		popNameScope();
	}
	
	
	/* Import statements. */
	
	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		
		importNamespace(Utilities.createNameFromPath(node.getId()));
	}
	
	
	/* Task declarations. */
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		
		TId id = node.getName();
		NameScope scope = createNameScope(id, node);
		
		POscope p = node.getOscope();
		if (p instanceof APublicOscope) scope.getSelfEntry().setDeclaredPublic();
		
		resolveForwardReferences(scope.getSelfEntry());
	}
	
	public void outAOtaskDeclaration(AOtaskDeclaration node) {
		popNameScope();
	}
	
	
	/* Add function declarations to the namespace. */
	
	public void inAOfunctionDeclaration(AOfunctionDeclaration node)
	{
		TId id = node.getName();
		DeclaredEntry entry = new DeclaredEntry(id.getText(), getCurrentNameScope(), node);
		try {
			addSymbolEntry(entry, id);
		} catch (SymbolEntryPresent ex) {
			throw new RuntimeException(ex);
		}
		if (node.getOscope() instanceof APublicOscope) entry.setDeclaredPublic();
		resolveForwardReferences(entry);
	}

	public void outAOfunctionDeclaration(AOfunctionDeclaration node)
	{
		super.outAOfunctionDeclaration(node);
	}

	/**
	 * Map the node types that are used to specify a DABL expression type, to the
	 * actual value types that are defined by the language.
	 */
	public static ValueType mapTypeSpecToValueType(POtypeSpec typeSpec) {
		if (typeSpec instanceof ANumericOtypeSpec) return ValueType.numeric;
		if (typeSpec instanceof AStringOtypeSpec) return ValueType.string;
		if (typeSpec instanceof ALogicalOtypeSpec) return ValueType.logical;
		if (typeSpec instanceof AArrayOtypeSpec) return ValueType.array;
		throw new RuntimeException("Unexpected typespec: " + typeSpec.getClass().getName());
	}
}

