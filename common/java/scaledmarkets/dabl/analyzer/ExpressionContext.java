package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.AOidRef;
import java.util.HashMap;
import java.util.Date;

/**
 * Runtime context in which a Dabl expression is evaluated.
 */
public abstract class ExpressionContext {
	
	/**
	 * 
	 */
	public abstract Object getValueForVariable(String variableName);
	
	/**
	 * If the task has executed, return the process status that it returned.
	 * If the task has not executed, throw an exception.
	 */
	public abstract int getTaskStatus(String taskName) throws Exception;
	
	/**
	 * Return the most recent date/time at which the input or output represented
	 * by the specified identifier was changed.
	 */
	public abstract Date getDateOfMostRecentChange(AOidRef inputOrOutputName) throws Exception;
}
