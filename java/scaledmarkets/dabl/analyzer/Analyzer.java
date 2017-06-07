package scaledmarkets.dabl.analyzer;

import scaledmarkets.dabl.node.Switch;

public interface Analyzer extends Switch {
	CompilerState getState();
}
