package scaledmarkets.dabl.main;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class SymbolNotPresent extends Exception
{
	public SymbolNotPresent(String name)
	{
		super("There is no entry with name " + name);
	}
}
