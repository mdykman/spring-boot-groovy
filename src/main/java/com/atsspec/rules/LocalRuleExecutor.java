package com.atsspec.rules;

public interface LocalRuleExecutor extends RuleExecutor {
	public void defineConstant(String name,Object value);
}