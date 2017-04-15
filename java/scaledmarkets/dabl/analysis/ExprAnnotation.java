package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.node.*;

public class ExprAnnotation implements Annotation
{
	private Node node;
	private Object value;
	
	public ExprAnnotation(Node node, Object value) {
		this.node = node;
		this.value = value;
	}
	
	public Node getNode() { return node; }
	
	public Object getValue() { return value; }
}
