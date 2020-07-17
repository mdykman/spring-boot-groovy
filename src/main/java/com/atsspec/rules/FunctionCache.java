package com.atsspec.rules;

import groovy.lang.Script;

public interface FunctionCache {
	public Script getScript(String hash);
	public void setScript(String hash,Script script);
}
