package scaledmarkets.dabl.main;

import scaledmarkets.dabl.node.*;

public class ExprAnnotation extends Annotation
{
	private Node expr;
	private Object value;
	
	public ExprAnnotation(Node expr, Object value)
	{
		this.expr = expr;
		this.value = value;
	}
	
	public Object getValue() throws DynamicEvaluation
	{
		if (value instanceof DynamicValuePlaceholder) throw new DynamicEvaluation();
		return value;
	}

	/**
	 * A value could not be computed through static analysis because there is
	 * at least one method that must be evaluated at runtime.
	 */
	public static class DynamicEvaluation extends Exception
	{
		public DynamicEvaluation() { super(); }
	}
	
	public DynamicValuePlaceholder getDynamicValuePlaceholder()
	{
		if (! (value instanceof DynamicValuePlaceholder)) throw new RuntimeException("Unexpected");
		return (DynamicValuePlaceholder)value;
	}
	
	static class DynamicValuePlaceholder {}
}
