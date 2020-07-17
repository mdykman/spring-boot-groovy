

package com.atsspec.rules

import java.util.concurrent.ConcurrentHashMap

import org.apache.log4j.Logger

import groovy.sql.Sql

public class RuleBuilderFactory {
	
	Logger logger = Logger.getLogger(RuleBuilderFactory.class);
	private HashMap<String,RuleBuilder> map = new ConcurrentHashMap<>();
	String defaultRuleSets 

	public RuleBuilderFactory() {
		defaultRuleSets = System.getProperty("com.ats.rule.ruleset")
		if(defaultRuleSets == null) {
			defaultRuleSets = System.getenv("ATS_RULESET")
		} else {
			logger.info(String.format("default rule set configured from property com.ats.rule.ruleset: %s",defaultRuleSets))
		}
		if(defaultRuleSets == null) {
			throw new InstantiationException("no default ruleset defined");
		} else {
			logger.info(String.format("default rule set configured from environment variable ATS_RULESET: %s",defaultRuleSets))
		}
	}
	
	public RuleBuilder fetchRuleBuilder(Sql sql,String...rulesets=null) {
		String dx = rulesets == null ? defaultRuleSets : rulesets.join(",");
		RuleBuilder rb = map.get(dx);
		if(rb == null) {
			synchronized (this) {
				rb = map.get(dx);
				if(!rb) {
					rb = new RuleBuilderImpl(dx.split("[,]"))
					rb.init(sql)
					map.put(dx,rb)
				}
			}
		}
		rb
	}
}