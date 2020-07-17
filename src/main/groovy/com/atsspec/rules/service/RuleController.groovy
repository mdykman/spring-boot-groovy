package com.atsspec.rules.service;


import groovy.sql.Sql;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import org.springframework.web.bind.annotation.RequestMapping;

import com.atsspec.rules.Rule;
import com.atsspec.rules.RuleEngineMetrics;
import com.atsspec.rules.RuleException;
import com.atsspec.rules.RuleExecutor;
import com.atsspec.rules.RuleFailureException;
import com.atsspec.rules.RuleBuilder;
import com.atsspec.rules.UploadRule
import com.atsspec.rules.ValidationObject
import com.codahale.metrics.*;
import com.codahale.metrics.annotation.*

@RestController
@RequestMapping(name="rule-controller",value="/")
public class RuleController {
	//	public static final String APPLICATION_JSON =  "application/json";

	String klassName = getClass().getName()
	@Autowired
	private ApplicationContext context
	
	// under the current design, this should never happen.  traps in instrument() prevent it
	@ExceptionHandler([RuleException.class])
	@ResponseBody
	public ResponseEntity<Object>  ruleerror(RuleException re) {
		re.printStackTrace(System.err)
		new ResponseEntity([message:"rule exception" + re.getLocalizedMessage()],HttpStatus.FAILED_DEPENDENCY)
	}

	@ExceptionHandler([Exception.class])
	@ResponseBody()
	public ResponseEntity<Object>  error(Exception e) {
		e.printStackTrace(System.err)
		new ResponseEntity([message:"general: " + e.getClass().getName() + ": "+ e.getLocalizedMessage()],
		HttpStatus.INTERNAL_SERVER_ERROR)
	}



	@GetMapping(path="categories",produces= "application/json;charset=utf8")
	def categoryies() {
		instrument("categories") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class);
			RuleBuilder.getCategories()
		}
	}

	@GetMapping(name="index", path="/" ,produces="application/json;charset=utf8")
	public ResponseEntity<Object> index() {
		instrument("index") {
			RequestMappingHandlerMapping mapping = context.getBean(RequestMappingHandlerMapping.class)
			def r = [:]
			mapping.getHandlerMethods().forEach({ k,v->
				RequestMappingInfo rmi = (RequestMappingInfo) k
				String name = rmi.getName()
				if(!name) name = rmi.hashCode().toString()
				def ff = { bb ->
					def x = bb['expressions']['mediaType'] 
					"${x['type'][0]}/${x['subtype'][0]}".toString() 
				}
				r[name] = [
					paths: rmi.getMethodsCondition().getMethods().collect {rm->rm.toString()}.join(',') 
						+ ' ' + rmi.getPatternsCondition().getPatterns().join(','),
					produces:ff(rmi.getProducesCondition())
				]
				if(rmi.getConsumesCondition()['expressions']['mediaType']) {
					r[name]['consumes']=ff(rmi.getConsumesCondition())
				}
			})
			r
		}
	}
	
	@PostMapping(name="add-category",path="categories" ,consumes="application/json",produces="application/json;charset=utf8")
	def addCategory(@RequestBody List<String> list) {
		instrument("add-category") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class);
			Sql db = context.getBean("connection",groovy.sql.Sql)
			for(String c:list) {
				RuleBuilder.addCategory(db,c)
			}
			[message:ok]
		}
	}
	@GetMapping(name="reload",path="reload" ,produces="application/json;charset=utf8")
	public ResponseEntity<Object> reload() {
		instrument("reload") {
			Sql db = context.getBean("connection",groovy.sql.Sql)
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class);
			RuleBuilder.init(db)
			[message:'ok']
		}
	}

	/// this is the only writer among the available end-points
	@PostMapping(name="attributes",path="attributes" ,consumes="application/json",produces="application/json;charset=utf8")
	public ResponseEntity<Object> loadAttributes(@RequestBody List<Object> list) {
		instrument("attributes") {
			Sql db = context.getBean("connection", Sql.class);
			def q = """insert into category_attributes (categoryId,name,required,type)
   values(:cid,:name,:req,:type)
"""
			list.each({ a->
				a.attributes.each({ k,v->
					if(!db.firstRow("select * from category_attributes where caame=tegoryId = :cid and name = :name",
					[cid:a.category,name:v.name]))
						db.executeInsert(q.toString(),[cid:a.category,name:v.name,req:1,type:'a'])
				})
			})
			[message:'ok']
		}
	}

	@PostMapping(name="evaluate",path="evaluate" ,consumes="application/json",produces="application/json;charset=utf8")
	public ResponseEntity<Object> evaluate(@RequestBody ValidationObject vo) {
		instrument("evaluate") {
			Sql db = context.getBean("connection", Sql.class);
			RuleExecutor re = context.getBean("executor", RuleExecutor.class)
			re.validate(db,vo.getProducts())
		}
	}


	@PostMapping(name="validate",path="validate" ,consumes="application/json",produces="application/json;charset=utf8")
	public ResponseEntity<Object> validate(@RequestBody List<Object> list) {
		instrument("validate") {
			Sql db = context.getBean("connection", Sql.class);
			RuleExecutor re = context.getBean("executor", RuleExecutor.class)
			re.validate(db,list);
		}
	}

	@GetMapping(name="show-rules",path="rule" ,produces="application/json;charset=utf8")
	public ResponseEntity<Object> showRules() {
		instrument("showRules") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class);
			RuleBuilder.getRuleMap()
		}
	}

	@GetMapping(name="show-category-rules",path="rule/{cat}" ,produces="application/json;charset=utf8")
	public ResponseEntity<Object> showRulesForCategory(@PathVariable("cat") String cat) {
		instrument("showRulesForCategory") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class)
			def m = RuleBuilder.getRuleMap()
			if(m.containsKey(cat)) {
				return m[cat]
			}
			Sql db = context.getBean("connection",Sql.class)
			m = RuleBuilder.getRule(db,cat)
			if(m) return new ResponseEntity(m,HttpStatus.OK)
			["message":"no rule for ${cat}".toString(),cat:cat ==null ? "(null)" : cat]
		}
	}

	@PostMapping(name="create-rule",path="rule" ,consumes="application/json",produces="application/json;charset=utf8")
	public ResponseEntity<Object> createRule(
			@RequestBody UploadRule u) {
		instrument("createRule") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class)
			Sql sql = context.getBean("connection", Sql.class)
			int rid = RuleBuilder.addRule(sql,
					u.rule.ruleGroup, u.rule.expr, u.rule.description,u.rule.weight)
			def cc = new String[u.categories.size()];
			RuleBuilder.associateCategories(sql, rid, u.categories.toArray(cc))
			RuleBuilder.init(sql)
			u.rule
		}
	}

	@PutMapping(name="modify-rule",path="rule/{uuid}",consumes="application/json",produces="application/json;charset=utf8")
	public ResponseEntity<Object> updateRule(
			@RequestBody UploadRule u,
			@PathVariable("uuid") String uuid) {
		instrument("updateRule") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class)
			Sql sql = context.getBean("connection", Sql.class)
			UUID ruuid = UUID.fromString(uuid)
			boolean res=RuleBuilder.deleteRule(sql,ruuid)
			if(res) return createRule(u).getBody()
			new ResponseEntity(["message":"rule " + uuid + " not found"],HttpStatus.NOT_FOUND)
		}
	}

	@RequestMapping(name="delete-rule" ,method=RequestMethod.DELETE,path="rule/{uuid}",produces="application/json;charset=utf8")
	ResponseEntity<Object> deleteRule(
			@PathVariable("uuid") String uuid) {
		instrument("deleteRule") {
			RuleBuilder RuleBuilder = context.getBean("RuleBuilder", RuleBuilder.class)
			Sql sql = context.getBean("connection", Sql.class)
			def r = RuleBuilder.deleteRule(sql,UUID.fromString(uuid))
			if(r) return [message: 'ok']
			new ResponseEntity(["message":"rule " + uuid + " not found"],HttpStatus.NOT_FOUND)
		}
	}

	protected ResponseEntity<Object> instrument(String name,Closure closure) {
		MetricRegistry metricRegistry = context.getBean("metricRegistry",MetricRegistry.class)

		Counter acounter = metricRegistry.counter(MetricRegistry.name(klassName,name,"requests"))
		acounter.inc()
		Timer.Context tc = metricRegistry.timer(MetricRegistry.name(klassName,name,"execution")).time()
		try {
			Object o = closure()
			if(o instanceof ResponseEntity) return o
			return new ResponseEntity<>(o,HttpStatus.OK);
		} catch(RuleFailureException e) {
			metricRegistry.counter(MetricRegistry.name(klassName,name,"ruleexceptions")).inc()
			return new ResponseEntity<>([failed:e.failed,passed:e.passed],HttpStatus.CONFLICT)
		} catch(Exception e) {
			metricRegistry.counter(MetricRegistry.name(klassName,name,"exceptions")).inc()
			throw e
		} finally {
			acounter.dec()
			tc.stop()
		}
	}


}
