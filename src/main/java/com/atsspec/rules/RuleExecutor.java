package com.atsspec.rules;

import java.util.List;
import java.util.Map;

import groovy.sql.Sql;

public interface RuleExecutor {
	public Map<String, List<Object>> validate(Sql db,List<Object> inp);
	public void missingAttribute(int categoryId,String name) ;
	public void missingProduct(int categoryId); 
	public void isRequired(int product);

}
