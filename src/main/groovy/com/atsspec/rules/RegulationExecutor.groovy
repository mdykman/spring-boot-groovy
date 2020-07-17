package com.atsspec.rules

class RegulationExecutor extends SimpleExecutor implements LocalRuleExecutor {
	Map<String,Object> vars = new HashMap<>()
	
	public void defineConstant(String name,Object value ) {
		vars.put(name,value)
	}
	
	def baseEnvironment() {
		def h = super.baseEnvironment()
		vars.each({ k,v->
			h[k]=v
		})
		h
	}
}