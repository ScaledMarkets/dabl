package scaledmarkets.dabl.task;

public interface FunctionHandler {
	void callFunction(String funcNativeName, Object[] args,
		....targetVariableRef) throws Exception;
}
