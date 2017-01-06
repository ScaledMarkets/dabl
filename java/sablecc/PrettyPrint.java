package sablecc;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

/**
 * Output a hierarchical depication of a SableCC AST.
 */
public class PrettyPrint extends DepthFirstAdapter
{
	StringBuffer buff = new StringBuffer();

	public static String pp(Start ast)
	{
		PrettyPrint p = new PrettyPrint();
		ast.apply(p);
		return p.buff.toString();
	}
	
    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
    	System.out.println(getTabs(getDepth(node)) + node.getClass().getName() + ": " + node.toString());
		buff.append(node.getClass().getName() + "\n");
    }
    
    protected int getDepth(Node node)
    {
    	int depth = 0;
    	for (Node parent = node; parent != null; parent = parent.parent()) depth ++;
    	return depth;
    }
    
	protected String getTabs(int depth)
	{
		if (depth < tabs.length) return tabs[depth];
		String s = "";
		for (int i = 0; i < depth; i++) s += "| ";
		s += "\u2514\u2500";
		return s;
	}
    
    private String[] tabs = {
    	"",
    	"\u2514\u2500",
    	"| \u2514\u2500",
    	"| | \u2514\u2500",
    	"| | | \u2514\u2500",
    	"| | | | \u2514\u2500",
    	"| | | | | \u2514\u2500",
    	"| | | | | | \u2514\u2500",
    	"| | | | | | | \u2514\u2500",
    	"| | | | | | | | \u2514\u2500",
    	"| | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500",
    	"| | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | \u2514\u2500"
	};
}
