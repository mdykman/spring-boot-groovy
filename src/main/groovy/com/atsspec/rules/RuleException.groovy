package com.atsspec.rules

public class RuleException extends Exception {
	
	public RuleException(String msg) {
		super(msg)
	}

	public RuleException(String msg,Exception e) {
		super(msg,e)
	}

} 