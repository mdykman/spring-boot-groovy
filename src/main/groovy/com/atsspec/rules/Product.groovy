package com.atsspec.rules

public class Product {
	def values
	def name
	def attributes
	RuleReporter ruleReporter
	RuleExecutor ruleExecutor
	
	Product(name,values) {
		this.name = name
		this.values = [:]
		values.each({k,v->
			this.values[k.toLowerCase()]=v
		})
		this.attributes  = [:]
		values['attributes'].each({ k,v->
			this.attributes[k.toLowerCase()]=v
		})
	}
  
	public RuleReporter getRuleReporter() {
		return ruleReporter
	}
	public void setRuleReporter(RuleReporter rr) {
		ruleReporter = rr
	}
	
	public RuleExecutor getRuleExecutor() {
		return ruleExecutor;
	}

	public void setRuleExecutor(RuleExecutor ruleExecutor) {
		this.ruleExecutor = ruleExecutor;
	}

	public java.lang.Object getName() {
		return name;
	}

	public void setProperty(String name,Object value) {
		throw new AssignmentException(this,name)
	}
	public void set(String k,Object value) {
		throw new AssignmentException(this,k);
	}
	public boolean hasAttribute(String name) {
		attributes.containsKey(attrName) || values.containsKey(attrName) 
	}
	public String getCategory() {
		return this.name
	}
	public int getCategoryId() {
		return values.category
	}

	public Object get( String name ) {
		return _get(name)
	}

	private Object _get( String attrName) {
		boolean unit=false; 
		if(attrName[0] == '$') {
			attrName=attrName.substring(1)
			unit=true
		}
		if(attributes.containsKey(attrName)) {
			if(unit) return attributes[attrName]?.unit ?: ''
			return attributes[attrName]?.value
		}
		if(unit) return ''
		if(values.containsKey(attrName)) {
			return values[attrName]
		}
		if(ruleReporter) {
			//if(!justAsking) 
				ruleReporter.missingAttribute(values.category,attrName)
			// else ruleReporter.warn("returning NullAttribute for ${name} ${values.category}")
		}
		return new NullAttribute(this,name)
	}

	def asBoolean() {
		true
	}
}
