package scaledmarkets.dabl.handlers;

import scaledmarkets.dabl.task.FunctionHandler;
import scaledmarkets.dabl.util.Utilities;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;

/**
 * For calling a Java function from a DABL script.
 */
public class JavaFunctionHandler implements FunctionHandler {
	
	public void callFunction(String funcNativeName, Object[] args,
		Object[] targetVariableRef // an array of size 1
		) throws Exception {
		
		// Parse the parts of the function name.
		int pos = funcNativeName.lastIndexOf(".");
		Utilities.assertThat(pos > 0, "Function name does not appear to contain its class name");
		String functionClassName = funcNativeName.substring(0, pos);
		Utilities.assertThat(functionClassName.length() > 0, "No function class name found");
		String functionSimpleName = funcNativeName.substring(pos+1);
		Utilities.assertThat(functionSimpleName.length() > 0, "No function name found");
		
		// Load the class. Use the current class loader. Thus, the function class
		// must be in the container TaskExecutor's classpath. This is the case
		// for all built-in Java functions.
		ClassLoader classLoader = this.getClass().getClassLoader();
		Class functionClass = Class.forName(functionClassName, true, classLoader);
		
		// Construct an array of Java types (classes), to match the argument types.
		Class[] argumentTypes = new Class[args.length];
		int i = 0;
		for (Object arg : args) {
			argumentTypes[i++] = arg.getClass();
		}
		
		// Verify that the function exists in the class.
		Method method = functionClass.getMethod(functionSimpleName, argumentTypes);
		
		// Verify that the method is a static method.
		Utilities.assertThat((method.getModifiers() & Modifier.STATIC) != 0,
			"Method " + funcNativeName + " is not static");
		
		// Call the function on the class, passing the argument values.
		Object result;
		try {
			result = method.invoke(null, args);
		} catch (InvocationTargetException ex) {
			Throwable cause = ex.getCause();
			if (cause instanceof RuntimeException) throw (RuntimeException)cause;
			else if (cause instanceof Error) throw (Error)cause;
			else throw (Exception)cause;
		}
		
		// Place the return result into the target variable.
		if (Void.class.isAssignableFrom(method.getReturnType())) {
			targetVariableRef[0] = result;
		} else {
			targetVariableRef[0] = null;
		}
	}
}
