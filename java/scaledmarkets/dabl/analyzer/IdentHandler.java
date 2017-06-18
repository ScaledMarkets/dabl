package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.util.List;

/**
 * An IdentHandler is attached to enclosing scopes when a symbol is not recognized
 * but might be defined later in an enclosing scope. Later, when the symbol is
 * defined, attached Handlers are checked to see if any refer to the symbol. If so,
 * the Handler's resolveRetroactively method is called, to resolve the original
 * symbol reference.
 */
public abstract class IdentHandler
{
	private DablBaseAdapter analyzer;
	private List<TId> path;
	private NameScope originalScope;
	
	public IdentHandler(DablBaseAdapter analyzer, List<TId> path, NameScope scope)
	{
		this.analyzer = analyzer;
		this.path = path;
		if (path.size() == 0) throw new RuntimeException("Unexpected");
		this.originalScope = scope;
		attachToEnclosingScopes();
	}
	
	/**
	 * This method is expected to perform all reference fixing.
	 */
	public abstract void resolveRetroactively(DeclaredEntry entry);
	
	public List<TId> getPath() { return path; }
	
	public NameScope getOriginalScope() { return originalScope; }
	
	protected TId getId() { return path.get(path.size()-1); }
	
	/**
	 * If the path is now resolvable, from its original context (scope), then perform
	 * the required actions to hook up prior references to the newly defined
	 * symbol. This method should be called at the end of each Analyzer method
	 * that defines a new symbol (creates a new SymbolTable entry) for all of
	 * its attached IdentHandlers.
	 */
	public boolean checkForPathResolution(DeclaredEntry entry)
	{
		if (this.analyzer.resolveSymbol(path, this.originalScope) != null)
		{
			resolveRetroactively(entry);
			removeFromAllScopes();
			return true;
		}
		return false;
	}
	
	/**
	 * Attach this IdentHandler to all the NameScopes that enclose the caller.
	 */
	protected void attachToEnclosingScopes()
	{
		for (NameScope scope = this.originalScope;
			scope != null;
			scope = scope.getParentNameScope())
		{
			scope.addIdentHandler(getId(), this);
		}
		analyzer.addIdentHandler(this);
	}
	
	/**
	 * Remove this IdentHandler from all NameScopes to which it is attached.
	 */
	protected void removeFromAllScopes()
	{
		for (NameScope scope = this.originalScope;
			scope != null;
			scope = scope.getParentNameScope())
		{
			scope.removeIdentHandler(getId());
		}
		
		analyzer.removeIdentHandler(this);
	}
	
	/**
	 * To be called after all symbols have been identified. Called only on
	 * the remaining IdentHandlers.
	 */
	public void failure()
	{
		TId id = path.get(0);
		throw new RuntimeException(
			"Unrecognized identifier: " + Utilities.createNameFromPath(path) + 
			", at line " + id.getLine() + ", col " + id.getPos());
	}
}
