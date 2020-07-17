package com.atsspec.rules;

import groovy.sql.Sql;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;


public interface RuleBuilder {
	public Object getRules(boolean labels);
	public Object getRuleMap();
	public Object getRule(Sql db,String hash) ;
	public RuleBuilder init(Sql db) ;
	public boolean deleteRule(Sql sql,UUID uuid);
	public boolean hasRule(Sql sql,UUID uuid);
	public Collection<String>  getCategories();
	public boolean addCategory(Sql db, String cat);
	public int addRule(Sql db,String group,String text,String description,Float weight);
	public boolean associateCategories(Sql sql, int ruleId, String[] categories);

	public Map<Integer,Map<String,Boolean>> getCategoryAttributes() ;
	
	public String cleanAttributeName(String attr); //S throws IllegalAttributeNameException
}
