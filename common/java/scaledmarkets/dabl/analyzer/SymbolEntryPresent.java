package com.scaledmarkets.dabl.analyzer;

public class SymbolEntryPresent extends Exception
{
	public SymbolEntryPresent(String name)
	{
		super("There is already an entry present with name " + name);
	}
}
