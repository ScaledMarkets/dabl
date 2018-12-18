package com.scaledmarkets.dabl.analyzer;

import com.scaledmarkets.dabl.node.Switch;

public interface Analyzer extends Switch {
	CompilerState getState();
	NameScopeEntry getEnclosingScopeEntry();
	NameScope getNamespaceNamescope();
}
