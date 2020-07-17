package com.atsspec.rules

public class IllegalAttributeNameException extends RuleException {
	String name
	IllegalAttributeNameException(String name) {
		this.name=name
	}
}