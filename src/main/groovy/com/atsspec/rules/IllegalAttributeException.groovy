package com.atsspec.rules

public class IllegalAttributeException extends RuleException {	
	public int cid;
	public String name;
	public String desc;
	def rule
	
	public IllegalAttributeException(int cid,String name,String desc,rule) {
		super(desc)
		this.name=name
		this.cid = cid
		this.desc=desc
		this.rule=rule
	}
} 