package com.atsspec.rules

public class RuleParserException extends RuleConfigurationException {
	Exception exception
	public RuleParserException(Exception e,String m) {
		super(m)
		exception = e
	}
}