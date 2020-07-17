package com.atsspec.rules

import groovy.sql.Sql;

import java.lang.Exception
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

public class SimpleExecutor implements RuleReporter,RuleExecutor {
	def rules
	def RuleBuilder
	
	def cats = [:]
	def rcats= [:]
	def pmap = [:]
	def visitedRules = [:]
	def baseExecContext = [:]
	def missingProductList = [:]
	def missingAttributeList = [:]
	def requiredList = []
	def categoryAttributes = [:]
	def ruleDictionary=[:]
	def reporter = new Reporter();
	def dirtyRules = [:]
	def rulesFailed = [:]
	def rulesPassed = [:]
	@Autowired
	private ApplicationContext context;

	def safeAttributes = [:]

	boolean letFail = false

	def theRuleInEffect;

	public SimpleExecutor(RuleBuilder RuleBuilder) {
		this.RuleBuilder = RuleBuilder
		rules = RuleBuilder.rules
		RuleBuilder.cats.each({ k,v->
			if(k) {
				def kk = k.toLowerCase()
				cats[kk]=v
				rcats[v]=kk
			}
		})
		rules.each { k,rr ->
			rr.each { r ->
				ruleDictionary[r.uuid]=r
			}
		}
		ruleDictionary
		baseExecContext = baseEnvironment()
	}

	def setCategoryAttributes(categoryAttributes) {
		this.categoryAttributes = categoryAttributes
	}
	/*
	 static def sbase(cats,RuleReporter ruleReport,RuleExecutor ruleExecutor) {
	 def h=[:]
	 cats.each({ k,v ->
	 def np = new NullProduct(k)
	 np.setRuleReporter ruleReport
	 np.setRuleExecutor ruleExecutor
	 h[k] = np
	 })
	 h.putAll(loadMethods())
	 h
	 }
	 */

	def baseEnvironment() {
		def h=[executor:this]
		cats.each({ k,v ->
			def np = new NullProduct(k)
			np.setRuleReporter this
			np.setRuleExecutor this
			h[k] = np
		})
		h
	}
	/*
	 def __baseEnvironment() {
	 def h=[:]
	 cats.each({ k,v ->
	 def np = new NullProduct(k)
	 np.setRuleReporter this
	 np.ruleRxecutor this
	 h[k] = np
	 })
	 h.putAll(loadMethods())
	 h
	 }
	 */
	//	@DependsOn({"connection","RuleBuilder"})
	/*
	 @Bean(name="executor")
	 public Executor getExecutor() {
	 groovy.sql.Sql db = context.getBean(groovy.sql.Sql.class)
	 RuleBuilder builder = context.getBean(RuleBuilder.class)
	 return new Executor(db, builder);
	 }
	 */
	public void log(String msg)  {
		reporter.log(msg)
	}
	public void  warn(String msg)  {
		reporter.warn(msg)
	}
	public void  error(String msg)  {
		reporter.error(msg)
	}
	public void  notify(String msg)  {
		reporter.notify(msg)
	}

	def makeSafeAttribute(String product,String attr) {
		if(!safeAttributes.hasProperty(product)) safeAttributes[product] = []
		safeAttibutes[property] << attr
	}

	def isSafeAttribute(String property,String attr) {
		if(safeAttributes.hasProperty(product)) {
			return safeAttibutes[property].contains(attr)
		}
		false
	}

	public void isRequired(int product) {
		requiredList << rcats[product]
	}

	def missingAttribute(String category,String name) {
		def k = rcats[category]
		if(k) missingAttribute(k,name)
		else throw new IllegalCategoryException(category,"unknown category " + category) 
	}

	public void missingAttribute(int categoryId,String name) {
		if(categoryAttributes.containsKey(categoryId) && categoryAttributes[categoryId].containsKey(name)) {
			if(categoryAttributes[categoryId][name]) {
				throw new IllegalAttributeException(categoryId,name,
				"missing required attribute ${rcats[categoryId]}.${name}",null)
			} else {
				warn("optional attribute attribute ${rcats[categoryId]} (${categoryId}) - ${name}")
			}
		} else {
			throw new IllegalAttributeException(categoryId,name,
			"unknown attribute ${rcats[categoryId]}.${name}",null)
		}
		true
	}

	def missingProduct(String category) {
		missingProduct(cats[category])
	}
	public void missingProduct(int categoryId) {
		def c = rcats[categoryId]
		missingProductList[c] = categoryId
	}

	public Map loadMethods(GroovyShell sh) {
		def inp = getClass().getResourceAsStream('/rules.properties');
		if(inp == null) {
			throw new FatalRuleException('rules not found')
		}
		def fmap =[:]
		Properties p = new Properties();
		p.load((InputStream)inp)
		inp.close()

		String m=p.getProperty("methods")
		def mm=m.split(",")
		int i
		def methods=[:]
		for(i=0;i<mm.length;++i) {
			def inq
			try {
				inq = SimpleExecutor.class.getResourceAsStream("/${mm[i]}.groovy");
				def ss = sh.evaluate(new BufferedReader(new InputStreamReader(inq)))
				//				ss.setRuleExecutor(this)
				fmap[mm[i]] = ss
				inq.close()
			} catch(Exception e) {
				error("failed to load internal function ${mm[i]} ${e.getLocalizedMessage()}")
			}
		}
		return fmap
	}
	def allowFailure(String name) {
		letFail = true
	}
	def allowsFailure() {
		true
	}

	def isOptional(categoryId,String name) {
		if(!categoryAttributes[category].containsKey(name)) {
			throw new IllegalAttributeException(categoryId,name,"the attribute ${name} is unknown")
		}
	}
	
	public Map<String, List<Object>> validate(Sql db,List<Object> inp) {
		return __validate(db,inp)
	}

	class CategoryEvaluation {
		GroovyShell gs
		private Map<String,Float> ruleScores

		public CategoryEvaluation(GroovyShell gs,Map<String,Float> ruleScores) {
			this.ruleScores = ruleScores
			this.gs=gs
		}
		public float evaluate(rules) {
			try {
				__evaluate(rules)
			} catch(Exception e) {
				e.printStackTrace()
			}
		}
		public float __evaluate(rules) {
			boolean first = true
			def rmap=[:]
			StringBuilder sb = new StringBuilder()
			sb << '['
			rules.each { r ->
				rmap[r.uuid]=r
				if(!first)  sb << ','
				if(!ruleScores.containsKey(r.uuid)) {
					sb << '\'' << r.uuid << "':"
					sb << "( safeExec('${r.uuid}') { " << r.expr << " }) as Boolean"
				} else {
					sb << '\'' << r.uuid << "':" << ruleScores[r.uuid]
				}
				first = false
			}
			sb << ']'
			def m=gs.evaluate(sb.toString())
			float wt = 0.0
			float total = 0
			m.each { u,v->
				ruleScores[u]=v
				def w = rmap[u].weight
				// nulls indicate dirty rules and are omitted from scoring
				if(v!=null) {
					wt+=w
					total+=v?w:0
				}
			}
			wt == 0 ? 1.0 : total/wt
		}

	}

	def __validate(Sql db, items) {
		def execContext = [:]
		execContext.putAll(baseExecContext)
		def first = true
		def products = [:]
		def status
		items.each({ i->
			def cname = rcats[i.category]
			if(!cname) {
				return ["message":"an unknown (${i.category}) category was provided in an item",item:i]
			}
			def k = cname.toLowerCase()
			def p = new Product(k,i)
			p.setRuleReporter  this
			if(first) {
				first = false
				requiredList <<  k
			}
			products[k] = p
			execContext[k] = p
			categoryAttributes[i.category] = [:]
			def q= "select * from category_attributes where categoryId = :categoryId"
			db.rows(q,[categoryId:i.category]).each{ row ->
				categoryAttributes[i.category][row.name]=row.required
			}
		})

		def rulesSeen = [:]
		def ruleScore=[:]

		def ctx=[:]
		execContext.each({ k,v->
			ctx[k.toLowerCase()]=v
		})
		ctx.allProducts = items
		ctx.safeExec = new Object() {
			Boolean call(String uuid,Closure closure) {
				def rule=ruleDictionary[uuid]
				Boolean res=Boolean.FALSE
				try {
				// during execution, null is treated as false
				// at the manager level null falgs a dirty rule
					res=new Boolean(closure())
					
					if(res==null) res=Boolean.FALSE
					if(res) {
						rulesPassed[rule.uuid] = rule
					} else {
						rulesFailed[rule.uuid]=[
							error: rule.description,
							rule:rule]
					}
				} catch(IllegalAttributeException ae) {
					dirtyRules[rule.uuid] = [
						error:ae.getLocalizedMessage() ?: ae.getClass().getName(),
						rule:rule]
					res = null
				} catch(AssignmentException ae) {
					dirtyRules[rule.uuid] = [
						error:ae.getLocalizedMessage() ?: ae.getClass().getName(),
						rule:rule]
					res = null
				} catch(RuleException re) {
					rulesFailed[rule.uuid]=[
						error:re.getLocalizedMessage() ?: re.getClass().getName(),
						rule:rule,
						errorType:re.getClass().getName()]
				} catch(MissingPropertyException mpe) {
							dirtyRules[rule.uuid] = [
								error:"unknown property in rule: ${mpe.getProperty()}",
								rule:rule,
								message:mpe.getLocalizedMessage(),
								expectedType:mpe.getType()]
					res = null
				} catch(Exception e) {
					e.printStackTrace()
					dirtyRules[rule.uuid] = [
						error:"unknown error ${e.getLocalizedMessage()}".toString(),
						rule:rule,
						errorType:e.getClass().getName()]
					/*
			 rulesFailed[rule.uuid]=[
			 error:e.getLocalizedMessage().toString() ?: "(unknown)",
			 rule:rule]
			 */
				}
				res
			}
		}

		Binding binding = new Binding(ctx)
		GroovyShell gs = new GroovyShell(binding)
		def meth=loadMethods(gs)
		meth.each { k,o->
			binding.setProperty(k,o)
		}
		def categoriesVisited = []
		def categoryScores = [:]
		def categoryErrors = [:]


		def newResult = []
		while(true) {
			def vp = requiredList - categoriesVisited
			if(vp.size()==0) break;
			String cat = vp[0]
			categoriesVisited << cat
			def totalweight =  0
			def totalcatscore =  0

			CategoryEvaluation ce = new CategoryEvaluation(gs,ruleScore)
			categoryScores[cat] = ce.evaluate(rules[cats[cat]])
		}

		def ex = products.keySet() - requiredList
		def extra = [:]
		ex.each { x->
			extra[x]=cats[x]
		}
		def xy = [passed:rulesPassed,
			failed:rulesFailed,
			errors:dirtyRules,
			missing:missingProductList,
			extra:extra,
			scores:categoryScores]
		xy
	}

	def executeRule(rule,description) {
		try {
			res = gs.execute(rule.expr)
		} catch(java.lang.Exception e) {
			System.err.println "error in rule ${rule.description}, ignoring rule"
		}
	}
}

