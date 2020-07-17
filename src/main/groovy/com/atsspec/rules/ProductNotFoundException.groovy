package com.atsspec.rules

class ProductNotFoundException extends RuleException {
	def product
	ProductNotFoundException(Product product) {
		super(product.name  + " is not found")
		this.product = product
	}
}
