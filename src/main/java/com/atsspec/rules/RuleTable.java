package com.atsspec.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleTable {
	Map<String,Map<String,Object>> rules = new HashMap<>();
	Map<String,Integer> categories = new HashMap<>();
	Map<Integer,String> ncategories = new HashMap<>();

	/*
	public List<Map<String,Object>> getRulesForCategory() {
		return rules;
	}
	public void  setRules(List<Map<String,Object>> rules) {
		this.rules = rules;
	}
	*/
}
;