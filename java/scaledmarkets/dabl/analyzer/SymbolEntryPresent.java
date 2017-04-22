package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class SymbolEntryPresent extends Exception
{
	public SymbolEntryPresent(String name)
	{
		super("There is already an entry present with name " + name);
	}
}
