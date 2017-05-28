package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;

public abstract class IdentSemanticHandler {
	private POidRef idRef;
	
	IdentSemanticHandler(POidRef idRef) {
		this.idRef = idRef;
	}
	
	public abstract void semanticAction(DeclaredEntry entry);
}
