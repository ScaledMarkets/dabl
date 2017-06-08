package scaledmarkets.dabl.analyzer;

public class ClientState extends CompilerState {
	
	public ExprAnnotation getExprAnnotation(Node node) {
		return (ExprAnnotation)(this.getOut(node));
	}
}
