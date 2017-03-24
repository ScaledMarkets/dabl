package scaledmarkets.dabl.analysis;

public interface VisibilityChecker {
	/**
	 * Check if the symbol entry is visible within the specified scope.
	 */
	void check(NameScope scope, SymbolEntry entry);
}
