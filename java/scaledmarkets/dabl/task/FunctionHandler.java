package scaledmarkets.dabl.task;

/**
 * An adapter that enables the TaskExecutor to call a function in another language.
 */
public interface FunctionHandler {
	
	/**
	 * The target variable reference is expected to either be null, or be a
	 * reference to an array of length 1. If not null, the function result
	 * should be placed in the array element.
	 */
	void callFunction(String funcNativeName, Object[] args,
		Object[] targetVariableRef) throws Exception;
}
