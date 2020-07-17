package com.atsspec.rules

class RuleConfigurationException extends RuleException {
	String msg
	RuleConfigurationException(String msg) {
		super(msg)
		this.msg = msg
	}
}
