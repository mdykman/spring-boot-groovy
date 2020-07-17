package com.atsspec.rules

public class IllegalCategoryException extends RuleException {
	def name
	public IllegalCategoryException(String name,String msg) {
		super(msg)
		this.name = name
	}
}