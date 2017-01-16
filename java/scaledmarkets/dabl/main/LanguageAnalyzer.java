package scaledmarkets.dabl.main;

//import scaledmarkets.dabl.analysis.*;
//import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
//import scaledmarkets.dabl.parser.*;

import java.util.LinkedList;

/**
 * Perform symbol resolution and annotation:
 * 
 *	Create name scope for namespace and add it to the global scope.
 *	Concatenate imported namespaces.
 *	Create name scope for tasks and add it to the namespace.
 *	Add input and output names to their enclosing task's scope.
 *	Add function declarations to the namespace.
 *	Add files declarations to the namespace.
 *	Evaluate string literals and string expressions.
 */
public class LanguageAnalyzer extends DablBaseAdapter
{
	public LanguageAnalyzer(CompilerState state) {
		super(state);
		
		state.setGlobalScope(pushNameScope(new NameScope("Global", null, null)));
		assertThat(state.scopeStack.size() == 1);   // debug
	}
	
	
	/* Only onamespace and otask_declaration define name scopes. */
	
	public void inAOnamespace(AOnamespace node) {
		LinkedList<TId> path = node.getPath();
		String name = createNameFromPath(path);
		
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = new NameScope(name, node, enclosingScope);
		SymbolEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		pushNameScope(newScope);
	}
	
	public void outAOnamespace(AOnamespace node) {
		popNameScope();
	}
	
	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		
		LinkedList<TId> importedNamespacePath = node.getId();
		
		//....
		
		
		//state.globalScope.getSymbolTable().appendTable(....);
	}
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		
		String name = node.getName().getText();
		NameScope enclosingScope = getCurrentNameScope();
		NameScope newScope = new NameScope(name, node, enclosingScope);
		SymbolEntry entry = new NameScopeEntry(newScope, name, enclosingScope);
		try { enclosingScope.addEntry(name, entry); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		pushNameScope(newScope);
	}
	
	public void outAOtaskDeclaration(AOtaskDeclaration node) {
		popNameScope();
	}
	
	
	/* Add input and output names to their enclosing task's scope. */
	
    public void inANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
    {
        ....
    }

    public void outANamedOnamedArtifactSet(ANamedOnamedArtifactSet node)
    {
        ....
    }
	
	
	/* Add function declarations to the namespace. */
	
    public void inAOfunctionDeclaration(AOfunctionDeclaration node)
    {
        ....
    }

    public void outAOfunctionDeclaration(AOfunctionDeclaration node)
    {
        ....
    }
	
	
	/* Add files declarations to the namespace. */
	
    public void inAOfilesDeclaration(AOfilesDeclaration node)
    {
        ....
    }

    public void outAOfilesDeclaration(AOfilesDeclaration node)
    {
        ....
    }
	
	
	/* Evaluate string literals.
		ostring_literal =
			{string} string
		  | {string2} string2
		
		String token definitions:
		string = quote [ any_character - quote ]* quote;
		string2 = triplequote any_character* triplequote;
	 */
	
    public void inAStringOstringLiteral(AStringOstringLiteral node)
    {
        super.inAStringOstringLiteral(node);
    }

    public void outAStringOstringLiteral(AStringOstringLiteral node)
    {
        ....
    }

    public void inAString2OstringLiteral(AString2OstringLiteral node)
    {
        super.inAString2OstringLiteral(node);
    }

    public void outAString2OstringLiteral(AString2OstringLiteral node)
    {
        ....
    }
	
    /* Evaluate string expressions.
     * Production:
     * ostring_literal =
     *	{static_string_expr} [left]:ostring_literal [right]:ostring_literal
     */
     
    public void inAStaticStringExprOstringLiteral(AStaticStringExprOstringLiteral node)
    {
        super.inAStaticStringExprOstringLiteral(node);
    }

    public void outAStaticStringExprOstringLiteral(AStaticStringExprOstringLiteral node)
    {
        ....
    }
	
	
	/* Utilities */
	
	String createNameFromPath(LinkedList<TId> path) {
		String name = "";
		boolean firstTime = true;
		for (TId id : path) {
			if (firstTime) firstTime = false;
			else name = name + ".";
			name = name + id.getText();
		}
		return name;
	}
}
