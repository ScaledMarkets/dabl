package scaledmarkets.dabl.handlers;

import scaledmarkets.task.FunctionHandler;
import scaledmarkets.util.Utilities;
import java.lang.reflect.Method;

/**
 * For calling a Java function from a DABL script.
 */
public class JavaFunctionHandler implements FunctionHandler {
	
	public void callFunction(String funcNativeName, Object[] args,
		Object[] targetVariableRef) throws Exception {
		
		// Parse the parts of the function name.
		int pos = funcNativeName.lastIndexOf(".");
		Utilities.assertThat(pos > 0, "Function name does not appear to contain its class name");
		String functionClassName = funcNativeName.substring(0, pos);
		Utilities.assertThat(functionClassName.size() > 0, "No function class name found");
		String functionSimpleName = funcNativeName.substring(pos+1);
		Utilities.assertThat(functionSimpleName.size() > 0, "No function name found");
		
		// Load the class.
		Class functionClass = Class.forName(functionClassName, boolean initialize, ClassLoader loader));
		
		// Verify that the function exists in the class, and that it is static.
		Method method = functionClass.getMethod(String name, Class<?>... parameterTypes);
		
		// Call the function on the class, passing the argument values.
		Object result = method.invoke(Object obj, Object... args);
		
		// Place the return result into the target variable.
		if (Void.class.isAssignableFrom(method.getReturnType())) {
			targetVariableRef[0] = result;
		} else {
			targetVariableRef[0] = null;
		}
	}
}
