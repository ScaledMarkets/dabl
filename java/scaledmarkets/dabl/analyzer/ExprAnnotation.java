package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.*;

public class ExprAnnotation implements Annotation
{
	private Node node;
	private Object value;
	private ValueType valueType;
	
	public ExprAnnotation(Node node, Object value, ValueType valueType) {
		this.node = node;
		this.value = value;
		this.valueType = valueType;
	}
	
	public Node getNode() { return node; }
	
	/**
	 * May return null.
	 */
	public Object getValue() { return value; }
	
	public ValueType getType() { return valueType; }
}
