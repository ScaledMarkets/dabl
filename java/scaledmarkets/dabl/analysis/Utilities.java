package scaledmarkets.dabl.analysis;

import scaledmarkets.dabl.node.*;
import java.util.List;

public class Utilities {
	
	public static String createNameFromPath(List<TId> path) {
		String name = "";
		boolean firstTime = true;
		for (TId id : path) {
			if (firstTime) firstTime = false;
			else name = name + ".";
			name = name + id.getText();
		}
		return name;
	}
}
