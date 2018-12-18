package com.scaledmarkets.dabl;

/**
 * A "back end" for a DABL compiler should implement this interface.
 */
public interface Executor {
	
	void execute() throws Exception;
}
