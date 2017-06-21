package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analyzer.DeclaredEntry;
import scaledmarkets.dabl.helper.Helper;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the Dabl process.
 */
public class DablContext extends ExpressionContext {
	
	DablContext(Helper helper) {
		this.helper = helper;
	}
	
	private Map<String, Integer> taskStatus = new HashMap<String, Integer>();
	private Helper helper;
	
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
		
		if (namedArtifactSet instanceof ANamedOnamedArtifactSet) {
		} else if (namedArtifactSet instanceof ARefOnamedArtifactSet) {
			POidRef idRef = ((ARefOnamedArtifactSet)namedArtifactSet).getOidRef();
			DeclaredEntry entry = this.helper.getDeclaredEntryForIdRef((AOidRef)idRef);
			if (entry == null) throw new RuntimeException(
				"No symbol entry found for " + idRef.toString());

			// Find the corresponding artifact set declaration, and create a
			// pattern set for that.
			Node artifactDef = entry.getDefiningNode();
			if (artifactDef instanceof POnamedArtifactSet) {
				namedArtifactSet = (POnamedArtifactSet)artifactDef;
			} else throw new RuntimeException(
				"artifact def is an unexpected type: " + artifactDef.getClass().getName());
		
		} else throw new RuntimeException(
			"named artifact set is an unexpected type: " + namedArtifactSet.getClass().getName());
		
		if (! (namedArtifactSet instanceof ANamedOnamedArtifactSet)) throw new RuntimeException(
			"Unexpected type for named artifact set: " + namedArtifactSet.getClass().getName());
		
		POartifactSet artifactSet = 
			((ANamedOnamedArtifactSet)namedArtifactSet).getOartifactSet();
		
		/* Obtain the namespace name, and the name of the task that owns the
			input or output. */
		
		// Get the input/output''s enclosing scope - should be a task.
		....
		// Get the task''s name.
		String taskName = ....
		
		// Get task''s enclosing scope - should be a namespace.
		....
		// Get the namespace''s name.
		String namespaceName = ....
		
		PatternSets patternSets = PatternSets.convertArtifactSetToPatternSets(
			namespaceName, taskName, artifactSet);
			
		// Examine each file and determine the date of the most recent change.
		return patternSets.getRepo().getDateOfMostRecentChange(patternSets);
	}
}
