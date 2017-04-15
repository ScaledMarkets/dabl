package scaledmarkets.dabl.analysis;

public abstract class ExprAnnotation implements Annotation
{
	private Node node;
	private Object value;
	
	public ExprAnnotation(Node node, Object value) {
		this.node = node;
		this.value = value;
	}
}
