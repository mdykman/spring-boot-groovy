package com.atsspec.rules

import groovy.json.*
import groovy.sql.Sql

jdbcUrl = 'jdbc:mysql://localhost/ats_rules'

def db = Sql.newInstance(jdbcUrl,'root','password')

def aa = new JsonSlurper().parse(new InputStreamReader(System.in))

def q = """insert into category_attributes (categoryId,name,required,type)
   values(:cid,:name,:req,:type)
on duplicate key ignore
"""
aa.each({ a->
	a.attributes.each({ k,v->
		db.executeInsert(q,[cid:a.category,name:k,req:1,type:'a'])
	})
})
