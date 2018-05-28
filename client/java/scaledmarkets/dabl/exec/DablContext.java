package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analyzer.NameScope;
import scaledmarkets.dabl.analyzer.DeclaredEntry;
import scaledmarkets.dabl.analyzer.ExpressionContext;
import scaledmarkets.dabl.helper.Helper;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the Dabl process.
 */
public class DablContext extends ExpressionContext {
	
	DablContext(ExecHelper helper) {
		this.helper = helper;
	}
	
	private Map<String, Integer> taskStatus = new HashMap<String, Integer>();
	private ExecHelper helper;
	
	public ExecHelper getExecHelper() { return helper; }
	
	public Object getValueForVariable(String variableName) {
		throw new RuntimeException("Variables are only available in a task's runtime context");
	}
	
	public int getTaskStatus(String taskName) throws Exception {
		Integer status = this.taskStatus.get(taskName);
		if (status == null) throw new Exception("Task has not executed");
		return status.intValue();
	}
	
	void setTaskStatus(String taskName, int status) {
		this.taskStatus.put(taskName, status);
	}
	
	/**
	 * Return the most recent date/time at which the input or output represented
	 * by the specified identifier was changed.
	 */
	public Date getDateOfMostRecentChange(AOidRef inputOrOutputName) throws Exception {
		
		// Identify the declaration of the input or output.
		POnamedArtifactSet namedArtifactSet =
			this.helper.getNamedArtifactDeclFromArtfiactRef(inputOrOutputName);
		POartifactSet artifactSet = this.helper.getArtifactSet(namedArtifactSet);
		
		/* Obtain the namespace name, and the name of the task that owns the
			input or output. */
		
		// Get the input/output''s enclosing scope - should be a task.
		NameScope taskScope = this.helper.getDeclaringScope(inputOrOutputName);
		this.helper.assertThat(taskScope != null,
			"Scope containing " + inputOrOutputName.toString() + " not found");
		
		// Get the task''s name.
		String taskName = taskScope.getName();
		
		// Get task''s enclosing scope - should be a namespace.
		NameScope namespaceScope = taskScope.getParentNameScope();
		this.helper.assertThat(namespaceScope != null,
			"Scope containing " + taskName + " not found");
		
		// Get the namespace''s name.
		String namespaceName = namespaceScope.getName();
		
		PatternSets patternSets = this.helper.convertArtifactSetToPatternSets(
			namespaceName, taskName, artifactSet);
			
		// Examine each file and determine the date of the most recent change.
		return patternSets.getRepo().getDateOfMostRecentChange(patternSets);
	}
}
