package com.atsspec.rules


class NullProduct extends Product {

	NullProduct(String name) { super(name,[attributes:[:]]) }
	def asBoolean() { 
		false 
	}
	/*
	public boolean isRequired() {
		false
	}
	*/
	

}
