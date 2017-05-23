package scaledmarkets.dabl.task;

/**
 * An adapter that enables the TaskExecutor to call a function in another language.
 */
public interface FunctionHandler {
	void callFunction(String funcNativeName, Object[] args,
		....targetVariableRef) throws Exception;
}
