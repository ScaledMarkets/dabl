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
 * Main entry point for DABL processor.
 * Args: (see displayInstructions() below).
 */
public class Dabl
{
	boolean print = false;
	boolean analyzeOnly = false;
	boolean printTrace = false;
	String filename = null;
	
	/**
	 Command line program.
	 */
	public static void main(String[] args) throws Exception {
		
		Dabl dabl = new Dabl();
		
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
			else if (arg.equals("-p") || arg.equals("--print")) dabl.print = true;
			else if (arg.equals("-a") || arg.equals("--analyzeonly")) dabl.analyzeOnly = true;
			else if (arg.equals("-t") || arg.equals("--trace")) dabl.printTrace = true;
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
				dabl.filename = arg;
			}
			argno++;
		}
		
		try { dabl.process(); }
		catch (Exception ex)
		{
			if (printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
		}
	}
	
	/**
	 * This is the actual DABL processor.
	 * The processing phases are described here:
	 	https://github.com/Scaled-Markets/dabl/tree/master/langref#processing-phases
	 */
	public void process() throws Exception {
		
		if (filename == null)
		{
			displayInstructions();
			return;
		}
		
		....Need to insert template processor here
		
		Lexer lexer = new Lexer(new PushbackReader(new FileReader(filename)));

		// Parse the DABL file.
		Parser parser = new Parser(lexer);
		Start ast = parser.parse();
		System.out.println("Syntax is correct");
		
		if (print) PrettyPrint.pp(ast);
		
		CompilerState state = new CompilerState();
		
		// Perform identifier matching and link things up.
		ast.apply(new Parser(state));
		
		// Expressions are evaluated where they appear; if an unquoted string
		// expression evaluates to a variable reference, then the variable's value
		// is used instead of the string. Note also that variables are only defined
		// in the return value of a function call. Prepositions that are defined in
		// function declarations are also recognized during this phase.
		ast.apply(new Elaborator(state));
		
		if (analyzeOnly) return;
		
		// Perform actions defined by the dabl file.
		ast.apply(new Executor(state));
	}
	
	static void displayInstructions()
	{
		System.out.println("Dabl version " + Config.DeclVersion + ". Usage:\n" +
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
