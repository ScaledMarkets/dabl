package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;

/**
 * Perform semantic actions when the specified symbol reference is evantually
 * declared. Semantic handlers are invoked when a declaration is recognized
 * and after the symbol reference has been matched up with the symbol declaration.
 * See the outRefNode method in DablBaseAdapter.
 */
public abstract class IdentSemanticHandler {
	private Node ref;  // a AOqualifiedNameRef or AOidRef
	
	IdentSemanticHandler(Node ref) {
    	Utilities.assertThat((node instanceof AOidRef) || (node instanceof AOqualifiedNameRef));
		this.ref = ref;
		registerSemanticHandlerFor(ref);
	}
	
	public abstract void semanticAction(DeclaredEntry entry);
}
