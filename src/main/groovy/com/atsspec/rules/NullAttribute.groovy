package com.atsspec.rules

public class NullAttribute implements Comparable {

	String name
	Product product
	public NullAttribute(Product product, String name) {
		this.product = product
		this.name = name
	}

	public String toString() {
		return ''
	}
	
	def asBoolean() {
		return false
	}

	def asType(java.lang.Class klass) {
		if(klass.isAssignableFrom(String.class)) return ''
		if(klass.isAssignableFrom(Integer.class)) return 0
		if(klass.isAssignableFrom(Long.class)) return 0L
		if(klass.isAssignableFrom(Float.class)) return Float.NaN
		if(klass.isAssignableFrom(Double.class)) return Double.NaN
		if(klass.isAssignableFrom(Boolean.class)) return false
		return false
	}

	def minus(a) { this } // - a.minus(b)
	def plus(a) { this }  // + a.plus(b)
	def multiply(a) { this } // * a.multiply(b)
	def div(a) { this }     // / a.div(b)
	def mod(a) { this } // % a.mod(b)
	def power(a) { this } // ** a.power(b)
	def or(a) { this }     // | a.or(b)
	def and(a) { this }      // | a.and(b)
	def xor(a) { this }//    ^ a.xor(b)
	def positive()  { this } // +a a.positive()
	def negative() { this }

	@Override
	public boolean equals(Object o) {
		return false;
	}
	@Override
	// never match anything
	public int compareTo(Object o) {
		return -1;
	}

}
