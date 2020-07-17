package com.atsspec.rules

import java.security.MessageDigest
import java.util.Collection
import java.util.List;
import java.util.Map;
import java.util.UUID

import groovy.sql.Sql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext;

class RuleBuilderImpl implements RuleBuilder {
	def rules
	def ruleSets
	def cats
	def rcats
	def attrs

	@Autowired
	ApplicationContext context

	public RuleBuilderImpl(String...sets) {
		this.ruleSets = sets
		this.rcats=[:]
	}
	
	public RuleBuilder init(Sql db) {
		this.cats = loadCategories(db)
		this.rules = loadRules(db)
		this.attrs = loadAttributes(db)
		this
	}
	public Collection<String>  getCategories() {
		this.cats.keySet()
	}

	public Map<Integer,Map<String,Boolean>> getCategoryAttributes() {
		return attrs
	}
	
	public boolean addCategory(Sql db, String cat) {
		def cc = new HashMap<String, Integer>()
		def l = db.rows('SELECT * from category where name = ?',[cat])
		if(!l.size()) {
			db.executeInsert("insert into category (name) values(?)", [cat]) 
			return true
		}
		return false
	}

	def loadCategories(Sql db) {
		def cc = new HashMap<String, Integer>()
		db.eachRow('SELECT * from category') { row->
			cc[row.name.toLowerCase()]=row.id
			this.rcats[row.id]=row.name.toLowerCase()
		}
		cc
	}

	def loadAttributes(Sql db) {
		def ca = [:]
		rcats.each{ id,name->
			ca[id] = [:]
			db.eachRow("select * from category_attributes where categoryId = :cid",[cid:id]) { r->
				try {
					ca[id][cleanAttributeName(r.name)]=r.required
				} catch(IllegalAttributeNameException e) {
					reporter.error("ignoring illegal attribute '${r.name}'");
				}
			}
		}
		ca
	}

	public Object getRuleMap() {
		return getRules(true)
	}
	public Object getRules(boolean labels=false) {
		if(labels) {
			def rs =[:]
			rules.each { k,v->
				rs[this.rcats[k]]=v
			}
			return rs
		}
		return  rules
	}

	public Object getRule(Sql db,String hash) {
		// TODO:: FIX THIS
		db.rows("select * from rule where hash = :hash",[hash:hash])
	}

	def loadRules(Sql db) {
		def rr = [:]
		def h = [:]
		def gr = ruleSets.collect({s->"'${s}'"}).join(',')
		def q  = """
select c.id as categoryId, c.name as name, rm.weight as ww ,r.* from category c
join rule_map rm on c.id = rm.categoryId
join rule r on rm.ruleId = r.id
  and ruleGroup in (${gr})
order by ordinal
"""
		def rows = db.rows(q.toString())
		rows.each({ c->
			if(!h[c.hash]) {
				h[c.hash]=c.expr
			}
			if(!rr[c.categoryId]) rr[c.categoryId]=[]
			rr[c.categoryId] << [expr:h[c.hash],hash:c.hash,uuid:c.uuid,description:c.description,
				weight: c.ww ?: c.weight,ordinal:c.ordinal]
		})

		rr
	}
	public boolean hasRule(Sql db,UUID uuid) {
		(boolean) db.firstRow("select id from rule where uuid= ?",[uuid.toString()])
	}
	public boolean deleteRule(Sql db,UUID uuid) {
		def r = db.firstRow("select id from rule where uuid= ?",[uuid.toString()])
		if(r) {
			def id = r.id
			db.execute("delete from rule_map where rule_id = ?",id)
			db.execute("delete from rule where id = ?",id)
			return true
		}
		false
	}
	public boolean associateCategories(Sql db, int ruleId,  String[] categories) {
		boolean done = false;
		categories.each { c->
			if(! cats[c]) throw new IllegalCategoryException("unknown category ${c}")
			def ff = db.firstRow("select count(*) as cc from rule_map where ruleId = ? and categoryId = ?",
				ruleId,cats[c]);
			if(!ff.cc) {
				done = true
				db.executeInsert("INSERT INTO rule_map(categoryId,ruleId,weight) values(?,?,?)",cats[c],ruleId,1)
			}
		}
		done
	}

	protected Rule readRule(Sql db,UUID uuid) {
		Rule rr
		db.firstRow("select * from rule where uuid = :uuid",[uuid:uuid]) { r-> rr = r }
		rr
	}

	public int addRule(Sql db,Rule rule) {
		if(rule.id) { // update
			def res = db.executeUpdate(
					"""update rules SET 
  hash = md5(:mexpr), 
  `uuid`=:uuid,
  expr=:expr,
  ruleGroup=:ruleGroup,
  description=:description,
  weight=:weight) where id = :id
""",[
	mexpr:rule.expr,
	uuid:rule.uuid,
	expr:rule.expr,
	ruleGroup:rule.ruleGroup,
	description:rule.description,
	weight:rule.weight,
	id:rule.id])

			return res[0][0]

		} else {
			return addRule(db,rule.ruleGroup,rule.expr,rule.description,rule.weight)
		}
	}
	public int addRule(Sql db,String group,String text,String description,Float weight) {
		text = text.trim().replaceAll(~/[ \t]/,' ')
		GroovyShell shell = new GroovyShell()
		try {
			SimpleExecutor executor = context.getBean("executor",SimpleExecutor.class)
			executor.loadMethods(shell)

			shell.parse(text)
		} catch(Exception e) {
			throw new RuleParserException(e,"error in expression: ${text}");
		}

		UUID uuid = UUID.randomUUID();
		def skip = false
		def rr = 0
		if(!weight) weight = 1.0;
		def res = db.executeInsert(
				"""INSERT INTO rule (`hash`, `uuid`,expr,ruleGroup,`description`) 
                   VALUES(md5(:mtext),:uuid,:text,:group,:description)
""", [mtext:text,uuid:uuid.toString(),text:text,group:group,description:description] )
		return res[0][0]

	}

	public String cleanAttributeName(String attr)
	{
		if(! attr =~ /[^a-zA-Z0-9\s]/) {
			throw new IllegalAttributeNameException(attr)
		}
		attr.replaceAll(/[\s]+/,'_')
	}

}
