package scaledmarkets.dabl.analyzer;

import java.util.List;

/**
 * Defines the expression value types that are permitted by the DABL language.
 */
public enum ValueType {
	string, numeric, logical, array;

	/**
	 * Verify that a list of actual types is compatible with the specified list
	 * of declared types.
	 */
	public static checkTypeListConformance(List<ValueType> declaredTypes,
		List<ValueType> actualTypes) throws Exception {
	
		Utilities.assertThat(declaredTypes.size() == actualTypes.size(),
			"Mismatch in number of actual (" + actualTypes.size() + ") and declared (" +
			declaredTypes.size() + ") arguments");
		
		int i = 0;
		for (ValueType declaredType : declaredTypes) {
			ValueType actualType = actualTypes.get(i++);
			Utilities.assertThat(actualType == declaredType,
				"Type " + actualType.toString() + " is not compatible with " +
				declaredType.toString());
		}
	}
}
