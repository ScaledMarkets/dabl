package com.scaledmarkets.dabl.analyzer;

public class SymbolNotPresent extends Exception
{
	public SymbolNotPresent(String name)
	{
		super("There is no entry with name " + name);
	}
}
