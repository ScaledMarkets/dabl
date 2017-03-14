package scaledmarkets.dabl;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;
import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.exec.*;
import scaledmarkets.dabl.Config;

import sablecc.PrettyPrint;

import java.io.Reader;
import java.io.PushbackReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;

/**
 * DABL command line processor.
 * Args: (see displayInstructions() below).
 */
public class Main
{
	/**
	 Command line program.
	 */
	public static void main(String[] args) throws Exception {
		
		Dabl dabl;
		String filename = null;
		boolean simulate = false;

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
			else 9f (arg.equals("-s") || arg.equals("--simulate")) simulate = true;
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
		
		if (filename == null) {
			displayInstructions();
			return;
		}
		
		// Parse input and perform analysis.
		System.out.println("Processing file " + filename + "...");
		dabl = new Dabl(new FileReader(filename));
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
		System.out.println("Performing actions...");
		TaskContainerFactory taskContainerFactory;
		if (simulate) taskContainerFactory = new TaskSimulatorFactory();
		else taskContainerFactory = new TaskDockerContainerFactory();
		Executor exec = new DefaultExecutor(state, taskContainerFactory);
		try {
			exec.execute();
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
			"\t\t\t-s or --simulate (simulate only - print tasks instead of executing them)\n" +
			"\t\t\t-h or --help"
			);
		return;
	}
}
