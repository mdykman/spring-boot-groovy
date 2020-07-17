package com.atsspec.rules;

public interface RuleReporter {
	public void log(String msg);
	public void warn(String msg);
	public void error(String msg);
	public void notify(String msg);
}
