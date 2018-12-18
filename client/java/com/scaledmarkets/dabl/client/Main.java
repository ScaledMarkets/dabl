package com.scaledmarkets.dabl.client;

import com.scaledmarkets.dabl.Config;
import com.scaledmarkets.dabl.Executor;
import com.scaledmarkets.dabl.analysis.*;
import com.scaledmarkets.dabl.lexer.*;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.parser.*;
import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.client.*;
import com.scaledmarkets.dabl.exec.*;

import java.io.Reader;
import java.io.FileReader;
import java.util.Set;
import java.util.HashSet;

/**
 * DABL command line processor.
 * Args: (see displayInstructions() below).
 */
public class Main
{
	/**
	 * Command line program.
	 * To do: Convert to use http://commons.apache.org/proper/commons-cli/
	 */
	public static void main(String[] args) throws Exception {
		
		Dabl dabl;
		String filename = null;
		boolean print = false;
		boolean printTrace = false;
		boolean analysisOnly = false;
		boolean simulate = false;
		boolean omitStandard = false;
		boolean verbose = false;
		Set<String> keepSet = new HashSet<String>();

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
			else if (arg.equals("-t") || arg.equals("--trace")) printTrace = true;
			else if (arg.equals("-a") || arg.equals("--analysis")) analysisOnly = true;
			else if (arg.equals("-s") || arg.equals("--simulate")) simulate = true;
			else if (arg.equals("-o") || arg.equals("--omitstd")) omitStandard = true;
			else if (arg.equals("-v") || arg.equals("--verbose")) verbose = true;
			else if (arg.equals("-k") || arg.equals("--keep")) {
				argno++;
				if (argno == args.length) {
					System.out.println("Missing argument for '-k/--keep' option");
					return;
				}
				keepSet.add(args[argno]);
			}
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
		Reader reader = new FileReader(filename);
		compile(print, printTrace, analysisOnly, simulate, omitStandard, verbose,
			keepSet, reader);
	}
	
	public static CompilerState compile(boolean print, boolean printTrace,
		boolean analysisOnly, boolean simulate, boolean omitPackageStandard, boolean verbose,
		Set<String> keepSet, Reader reader) throws Exception {
		
		Dabl dabl = new Dabl(print, printTrace, omitPackageStandard, reader);
		CompilerState state = null;
		try { state = dabl.process(); }
		catch (Exception ex)
		{
			if (printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
			System.exit(1);
		}
		
		if (analysisOnly) return state;
		
		// Perform actions.
		System.out.println("Performing actions...");
		TaskContainerFactory taskContainerFactory;
		if (simulate) taskContainerFactory = new TaskSimulatorFactory();
		else taskContainerFactory = new TaskDockerContainerFactory();
		Executor exec = new DefaultExecutor(state, taskContainerFactory,
			omitPackageStandard, verbose, keepSet);
		try {
			exec.execute();
		} catch (Exception ex) {
			if (printTrace) ex.printStackTrace();
			else System.out.println(ex.getMessage());
			System.exit(1);
		}
		
		return state;
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
			"\t\t\t-o or --omitstd (do not implicitly import package dabl.standard)\n" +
			"\t\t\t-v or --verbose (print details of each action performed)\n" +
			"\t\t\t-k <task-name> or --keep <task-name> (keep - i.e., don't delete - the\n" +
				"\t\t\t\tspecified container; option may be specified more than once)\n" +
			"\t\t\t-h or --help"
			);
		return;
	}
}
