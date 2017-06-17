package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.Node;

public class ClientState extends CompilerState {
	
	public ExprAnnotation getExprAnnotation(Node node) {
		return (ExprAnnotation)(this.getOut(node));
	}
}
