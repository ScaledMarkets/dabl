package scaledmarkets.dabl.main;

import scaledmarkets.decl.analysis.*;
import scaledmarkets.decl.lexer.*;
import scaledmarkets.decl.node.*;
import scaledmarkets.decl.parser.*;

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
