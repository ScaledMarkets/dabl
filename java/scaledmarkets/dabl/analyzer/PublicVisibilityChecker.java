package scaledmarkets.dabl.analyzer;

import java.util.List;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;

/**
 * A callback handler for checking if access is allowed to an element.
 * Used by outAOidRef and outAOqualifiedNameRef.
 */
public class PublicVisibilityChecker implements VisibilityChecker {
	
	PublicVisibilityChecker(List<TId> path) { this.path = path; }
	private List<TId> path;
	
	public void check(NameScope refScope, SymbolEntry entry) {
		NameScope referringNameScope = getNamespaceNameScope(refScope);
		NameScope entryNamespaceScope = getNamespaceNameScope(entry.getEnclosingScope());
		if (referringNameScope != entryNamespaceScope) {
			// referring scope is in a different namespace than entry name scope
			if (! entry.isDeclaredPublic()) {
				referringNameScope.printUpward();
				throw new RuntimeException(
					"Element " + Utilities.createNameFromPath(path) +
						" is referenced from " + referringNameScope.getName() + 
						" but is not public in " + entryNamespaceScope.getName());
			}
		}
	}
    
	/**
	 * Determine the name scope of the namespace that encloses the specified scope.
	 */
    static NameScope getNamespaceNameScope(NameScope scope) {
    	
    	for (NameScope s = scope;;) {
    		Node node = s.getNodeThatDefinesScope();
    		if (node instanceof AOnamespace) return s;
    		s = s.getParentNameScope();
    		if (s == null) break;
    	}
    	System.out.println("Namespace for scope " + scope.getName() + " not found");
    	scope.printUpward();  // debug
    	throw new RuntimeException("Namespace for scope " + scope.getName() + " not found");
    }
}
