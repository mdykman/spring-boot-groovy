package com.atsspec.rules


public class Attribute {
	def String product =null
	def String name = nu
	def String value = null
	def String expr = null
	boolean isExpression = false;
	boolean directValue = false;

	public Attribute(String expr) { this.expr = expr; isExpression = true }

  	public boolean equals(Object rhs) { }

	public Attribute(String product,String attr) {
		this.product = product
		this.name=attr
	}
	public Attribute(String product,String attr,String value) {
		this(product,attr)
		this.value=value
		this.directValue = true
	}
	
	public String getProduct() {
		product
	}
	
	public eval() {
		directValue ? value : isExpression ? expr : (product + '.' + name)  
	}

   def unaryfunc(op) {
		return { lhs->
			StringBuilder buf = new StringBuilder()
			buf << '(' << op << ' ' << this.eval() << ')'
			return new Attribute(buf.toString())
		}
	}
   def dyadicfunc(op) {
		return { lhs->
			StringBuilder buf = new StringBuilder()
			buf << '(' << this.eval() << ' ' << op << ' ' << lhs.eval() << ')'
			return new Attribute(buf.toString())
		}
	}
	def plus = dyadicfunc('+')  // + a.plus(b)
   def minus = dyadicfunc('-') // - a.minus(b)
   def multiply = dyadicfunc('*') // * a.multiply(b)
   def div = dyadicfunc('/')     // / a.div(b)
	def mod = dyadicfunc('MOD') // % a.mod(b)
   def power = dyadicfunc('**') // ** a.power(b)
	def or = dyadicfunc('OR')     // | a.or(b)
	def and  = dyadicfunc('AND')      // | a.and(b)
   def xor  = dyadicfunc('XOR')//    ^ a.xor(b)
   def positive  = unaryfunc('-')// -a a.negative()
// a[b] a.getAt(b)
                   //  a[b] = c a.putAt(b, c)
                      // a in b b.isCase(a)
//   def leftShift(b) {} // << a.leftShift(b)
//   def rightShift(b) {} // << a.rightShift(b)
                   // >>> a.rightShiftUnsigned(b)
                    // ++ a.next()
                   //-- a.previous()
                  // a() a.call()
                  // as a.asType(b)
                  // ~a a.bitwiseNegate()

  }
