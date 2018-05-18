package scaledmarkets.dabl.task;

import scaledmarkets.dabl.analyzer.LanguageCoreAnalyzer;
import scaledmarkets.dabl.analyzer.NamespaceProcessor;
import scaledmarkets.dabl.analyzer.ImportHandler;
import scaledmarkets.dabl.node.*;
import java.io.Reader;
import java.io.StringReader;

/**
 * Annotate function call arguments with their values.
 */
public class TaskProgramAnalyzer extends LanguageCoreAnalyzer {
	
	TaskProgramAnalyzer(TaskContext context, ImportHandler importHandler) {
		
		super(context, importHandler);
	}
	
	private int taskCount = 0;

	
	public void outAOnamespace(AOnamespace node) {
		assertThat(taskCount == 1, "Exactly one task must be specified: found " + taskCount);
	}
	
	
	/* Disallowed:
		artifact_decl
		repo_decl
		files_decl
		typographic_decl (ignored)
		translation_decl (ignored)
		*/
	
	public void inAOartifactDeclaration(AOartifactDeclaration node) {
		throw new RuntimeException("Not allowed: " + node.getClass().getName());
	}

	public void inAOrepoDeclaration(AOrepoDeclaration node) {
		throw new RuntimeException("Not allowed: " + node.getClass().getName());
	}

	public void inAOfilesDeclaration(AOfilesDeclaration node) {
		throw new RuntimeException("Not allowed: " + node.getClass().getName());
	}

	public void inAOtypographicDeclaration(AOfilesDeclaration node) {
		System.out.println("Ignoring: " + node.getClass().getName());
	}

	public void inAOtranslationDeclaration(AOfilesDeclaration node) {
		System.out.println("Ignoring: " + node.getClass().getName());
	}


	/*
	Allowed:
		import_decl
		task_decl
		function_decl
	*/

	public void inAImportOnamespaceElt(AImportOnamespaceElt node) {
		super.inAImportOnamespaceElt(node);
	}
	
	public void inAOtaskDeclaration(AOtaskDeclaration node) {
		super.inAOtaskDeclaration(node);
		taskCount++;
	}
	
	public void inAOfunctionDeclaration(AOfunctionDeclaration node) {
		super.inAOfunctionDeclaration(node);
	}
}
