
import com.atsspec.rules.*


return new Object() {
	// RuleExecutor executor
	/*
		def setRuleExecutor(RuleExecutor executor) {
			this.executor = executor
		}
	*/
		def call(lhs,rhs) {
		cmp(lhs,rhs) || cmp(rhs,lhs)
	}
	def cmp(lhs,rhs) {
		def l=lhs.toString()
		def r=rhs.toString()
		b=new StringBuilder()
		int i = 0;
		for(; i < l.size() ; ++i) {
			if(i < r.size() && l[i]!=r[i])  b << l[i] << r[i]
			else b << l[i]
		}
		for(; i < l.size() ; ++i) {
			b << r[i]
		}
		if(b.size() == 0) return true
		String s = b.toString().toLowerCase()
		if(s == 'mf') return true
		return false
	}

}
