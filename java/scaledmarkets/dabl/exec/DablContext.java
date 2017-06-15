package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * The execution context of the Dabl process.
 */
public class DablContext extends ExpressionContext {
	
	private Map<String, int> taskStatus = new HashMap<String, int>();
	
	public Object getValueForVariable(String variableName) {
		throw new RuntimeException("Variables are only available in a task's runtime context");
	}
	
	public int getTaskStatus(String taskName) throws Exception {
		Object obj = this.taskStatus.get(taskName);
		if (obj == null) throw new Exception("Task has not executed");
		if (!( obj instanceof Integer)) throw new RuntimeException(
			"Status of task " + taskName + " is not an integer");
		return ((Integer)obj).intValue();
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
			getHelper().getNamedArtifactDeclFromArtfiactRef(inputOrOutputName);
		
		if (namedArtifactSet instanceof ANamedOnamedArtifactSet) {
		} else if (namedArtifactSet instanceof ARefOnamedArtifactSet) {
			POidRef idRef = ((ARefOnamedArtifactSet)namedArtifactSet).getOidRef();
			DeclaredEntry entry = getHelper().getDeclaredEntryForIdRef((AOidRef)idRef);

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
			"Unexpected type for artifact set: " + artifactSet.getClass().getName());
		
		POartifactSet artifactSet = 
			((ANamedOnamedArtifactSet)namedArtifactSet).getOartifactSet();
		
		PatternSets patternSets = PatternSets.convertArtifactSetToPatternSets(artifactSet);
			
		// Examine each file and determine the date of the most recent change.
		return repo.getDateOfMostRecentChange(patternSets);
	}
}
