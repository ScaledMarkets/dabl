package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.util.Utilities;
import java.util.List;

/**
 * Defines the expression value types that are permitted by the DABL language.
 */
public enum TimeUnit {
	ms, sec, min, hours, days;
	
	public long convertToMs(double t) {
		return this.convertToMs(t);
	}
	
	public static double convertToMs(double t, TimeUnit tunit) {
		switch (tunit) {
		case ms:
			return t;
		case sec:
			return t * MillisecondsInASecond;
		case min:
			return t * MillisecondsInAMinute;
		case hours:
			return t * MillisecondsInAnHour;
		case days:
			return t * MillisecondsInADay;
		default: throw new RuntimeException("Unexpected TimeUnit value: " + tunit.toString());
		}
	}
	
	private static final long MillisecondsInASecond = 1000;
	private static final long MillisecondsInAMinute = 60 * MillisecondsInASecond;
	private static final long MillisecondsInAnHour = 60 * MillisecondsInAMinute;
	private static final long MillisecondsInADay = 24 * MillisecondsInAnHour;
}
