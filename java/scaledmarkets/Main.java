package scaledmarkets.dabl;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.gen.*;
import scaledmarkets.dabl.Config;

import sablecc.PrettyPrint;

import java.io.Reader;
import java.io.PushbackReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

/**
 * Main entry point for DABL processor.
 * Args: (see displayInstructions() below).
 */
public class Main
{
	/**
	 Command line program.
	 */
	public static void main(String[] args) throws Exception {
		
		Dabl dabl = new Dabl();
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
			else if (arg.equals("-p") || arg.equals("--print")) dabl.print = true;
			else if (arg.equals("-t") || arg.equals("--trace")) dabl.printTrace = true;
			else if (arg.equals("-a") || arg.equals("--analysis")) dabl.analysisOnly = true;
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
				dabl.reader = new FileReader(arg);
				System.out.println("Processing file " + filename);
			}
			argno++;
		}
		
		// Parse input and perform analysis.
		CompilerState state;
		try { state = dabl.process(); }
		catch (Exception ex)
		{
			if (dabl.printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
			System.exit(1);
		}
		
		if (analysisOnly) return;
		
		// Perform actions.
		TaskContainerFactory taskContainerFactory = new TaskContainerFactory();
		Generator gen = new DefaultGenerator(state, taskContainerFactory);
		try {
			gen.generate();
		} catch (Exception ex) {
			if (dabl.printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	static void displayInstructions()
	{
		System.out.println("Dabl version " + Config.DablVersion + ". Usage:\n" +
			"\tjava -jar dabl.jar [options] <filename>,\n" +
			"\t\twhere options can be\n" +
			"\t\t\t-p or --print (print the AST)\n" +
			"\t\t\t-t or --trace (print stack trace instead of just error msg)\n" +
			"\t\t\t-a or --analysis (analysis only - do not perform any actions)\n" +
			"\t\t\t-h or --help"
			);
		return;
	}
}
