import scaledmarkets.task.FunctionHandler;



public class JavaFunctionHandler implements FunctionHandler {
	
	public void callFunction(String funcNativeName, Object[] args,
		Object[] targetVariableRef) throws Exception {
		
		// Parse the parts of the function name.
		String functionClassName = ....
		
		String functionSimpleName = ....
		
		
		// Load the class.
		
		
		// Verify that the function exists in the class, and that it is static.
		
		// 
		
		
		// Call the function on the class, passing the argument values.
		
		
		// Place the return result into the target variable.
		
		
		
	}
}
