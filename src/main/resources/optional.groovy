
import com.atsspec.rules.*

return new Object() {
//	Executor executor
	// RuleExecutor executor
	/*
		def setRuleExecutor(RuleExecutor executor) {
			this.executor = executor
		}
	*/
	
	def call(Product product) {
	}
	def call(attr) {
		if(attr instanceof Product) {
			throw new RuleConfigurationException("Product (${product.name}) is not allowed in function `optional`");
		} else if(attr instanceof NullAttribute) {
		 }
		 return true
	}
}
