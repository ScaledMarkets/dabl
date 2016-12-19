package scaledmarkets.dabl.main;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Enumeration;
import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

/** Perform symbol resolution and annotation. */
public class LanguageAnalyzer extends DablBaseAdapter
{
	public LanguageAnalyzer(CompilerState state, List<String> providerPaths)
	{
		super(state);
		
		
		//getGlobalScope().getSymbolTable().print();
	}
	
	
	/* Perform final cleanup. */
	
}
