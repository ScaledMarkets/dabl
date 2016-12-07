package scaledmarkets.dabl.main;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import scaledmarkets.dabl.gen.*;
import scaledmarkets.dabl.Config;

import sablecc.PrettyPrint;

import java.io.PushbackReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

/**
 * Args: (see displayInstructions() below).
 */
public class Dabl
{
	public static void main(String[] args) throws Exception
	{
		boolean print = false;
		boolean analyzeOnly = false;
		boolean printTrace = false;
		List<String> providerPaths = new LinkedList<String>();
		String filename = null;
		
		int argno = 0;
		for (;;)
		{
			if (argno == args.length) break;
			String arg = args[argno];
			if (arg.equals("-h") || arg.equals("?") || arg.equals("-h") || arg.equals("--help"))
			{
				displayInstructions();
				return;
			}
			else if (arg.equals("-p") || arg.equals("--print")) print = true;
			else if (arg.equals("-a") || arg.equals("--analyzeonly")) analyzeOnly = true;
			else if (arg.equals("-t") || arg.equals("--trace")) printTrace = true;
			else if (arg.startsWith("-"))
			{
				System.out.println("Unrecognized option: " + arg);
				return;
			}
			else
			{
				if (filename != null)
				{
					System.out.println("Filename specified a second time: " + arg);
					return;
				}
				filename = arg;
			}
			argno++;
		}
		
		if (filename == null)
		{
			displayInstructions();
			return;
		}
		
		Lexer lexer = new Lexer(new PushbackReader(new FileReader(filename)));

		try
		{
			// Parse expression string.
			Parser parser = new Parser(lexer);
			Start ast = parser.parse();
			System.out.println("Syntax is correct");
			
			if (print) PrettyPrint.pp(ast);
			
			CompilerState state = new CompilerState();
			
			// Perform identifier matching and ink things up.
			ast.apply(new LanguageAnalyzer(state, providerPaths));
			
			// Perform logical integrity and secuirty analysis.
			ast.apply(new IntegrityAnalyzer(state));
			
			if (analyzeOnly) return;
			
			// Perform actions defined by the dabl file.
			ast.apply(new Generator(state));
		}
		catch (Exception ex)
		{
			if (printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
		}
	}
	
	static void displayInstructions()
	{
		System.out.println("Decl version " + Config.DeclVersion + ". Usage:\n" +
			"\tjava -jar dabl.jar [options] <filename>,\n" +
			"\t\twhere options can be\n" +
			"\t\t\t-p or --print (print the AST)\n" +
			"\t\t\t-a or --analyzeonly\n" +
			"\t\t\t-t or --trace (print stack trace instead of just error msg)\n" +
			"\t\t\t-h or --help"
			);
		return;
	}
}
