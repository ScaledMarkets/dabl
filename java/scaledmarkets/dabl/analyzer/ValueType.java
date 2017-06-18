package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.util.Utilities;
import java.util.List;

/**
 * Defines the expression value types that are permitted by the DABL language.
 */
public enum ValueType {
	string, numeric, logical, array;

	/**
	 * Verify that this type can be assigned to the specified type.
	 */
	public void checkTypeAssignabilityTo(ValueType targetType) throws Exception {
		
		Utilities.assertThat(this.equals(targetType),
			"Type " + this.toString() + " is not assignable to " +
			targetType.toString());
	}
	
	/**
	 * Verify that this type can be assigned from the specified type.
	 */
	public void checkTypeAssignabilityFrom(ValueType sourceType) throws Exception {
		
		Utilities.assertThat(this.equals(sourceType),
			"Type " + this.toString() + " is not assignable from " +
			sourceType.toString());
	}

	/**
	 * Verify that this type can be assigned from the specified native Java type.
	 */
	public void checkNativeTypeAssignabilityFrom(Class sourceNativeType) throws Exception {
		
		switch (this) {
			case string: Utilities.assertThat(String.class.isAssignableFrom(sourceNativeType),
				"Cannot assign " + sourceNativeType.getName() + " to string"); return;
			case numeric: Utilities.assertThat(Number.class.isAssignableFrom(sourceNativeType),
				"Cannot assign " + sourceNativeType.getName() + " to numeric"); return;
			case logical: Utilities.assertThat(Boolean.class.isAssignableFrom(sourceNativeType),
				"Cannot assign " + sourceNativeType.getName() + " to logical"); return;
			case array: Utilities.assertThat(sourceNativeType.isArray(),
				"Cannot assign " + sourceNativeType.getName() + " to array"); return;
			default: throw new RuntimeException(
				"Unexpected ValueType value: " + this.toString());
		}
	}

	/**
	 * Verify that a list of actual types can be assigned to the specified
	 * declared types.
	 */
	public static void checkTypeListAssignabilityTo(List<ValueType> sourceTypes,
		List<ValueType> targetTypes) throws Exception {
	
		Utilities.assertThat(sourceTypes.size() == targetTypes.size(),
			"Mismatch in number of source (" + sourceTypes.size() + ") and target (" +
			targetTypes.size() + ") arguments");
		
		int i = 0;
		for (ValueType targetType : targetTypes) {
			ValueType sourceType = sourceTypes.get(i++);
			sourceType.checkTypeAssignabilityTo(targetType);
		}
	}
}
