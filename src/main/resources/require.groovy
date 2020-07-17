import com.atsspec.rules.*

return { o->
	if(o instanceof NullProduct) {
		executor.missingProduct(o.getName())
		return false
	}
	else if(o instanceof Product) {
		executor.isRequired(o.getCategoryId())
	}
	true
}
