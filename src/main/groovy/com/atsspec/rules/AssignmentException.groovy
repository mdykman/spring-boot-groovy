package com.atsspec.rules

class AssignmentException extends RuleException {
	Product product
	String attr
	AssignmentException(Product product,String attr) {
		super("illegal assignmnt to ${product.name}.${attr}")
		this.attr = attr
		this.product = product
	}
}
