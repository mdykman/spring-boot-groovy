package com.atsspec.rules;

public class FatalRuleException extends RuleException {
	public FatalRuleException(String msg) {
		super(msg)
	}

	public FatalRuleException(String msg,Exception e) {
		super(msg,e)
	}
}
