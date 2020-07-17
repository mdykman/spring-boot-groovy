package com.atsspec.rules;

public interface ExecutionEnvironment {
	public void missingAttribute(int categoryId,String name);
	public void missingProduct(int categoryId);
	public void log(String s);
	public void error(String s);
	public void warn(String s);
	public void notify(String s);
}
