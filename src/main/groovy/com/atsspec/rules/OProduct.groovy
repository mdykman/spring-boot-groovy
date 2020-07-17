package com.atsspec.rules

import groovy.sql.Sql

public class OProduct extends Product {
	Product product
	def categoryName


	def static OProduct createOProduct(Sql db,Product product) {
		def name = db.firstRow("select name from category where categoryId  :cateogryId",[categoryId:product.categoryId])
		return new OProduct(product,name)
	}
	public OProduct(Product product,String name) { 
		this.product = product 
		this.categoryName = name
	}
	def get(String name) {
		if(product.hasAttribute(name)) {
			return new Attribute(categoryName,name,product.get(name));
		}
		return new Attribute(categoryName,name);
	}
}

