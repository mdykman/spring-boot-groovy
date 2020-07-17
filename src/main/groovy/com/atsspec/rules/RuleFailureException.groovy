package com.atsspec.rules

import org.springframework.http.HttpStatus;

public class RuleFailureException extends RuleException {
	def failed
	def passed
	int status
	
	public RuleFailureException (f,pl,HttpStatus s=HttpStatus.PRECONDITION_FAILED) {
		super("rule failed " + f.uuid)
		failed = f
		passed=pl
		status = s
	}
}
