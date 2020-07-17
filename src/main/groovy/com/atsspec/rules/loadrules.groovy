package com.atsspec.rules

import java.security.MessageDigest
import groovy.sql.Sql
import com.mysql.jdbc.*


import groovy.json.*






//println options.arguments()
//println options.r

jdbcUrl = 'jdbc:mysql://localhost/ats_rules'
 
conn = Sql.newInstance(jdbcUrl,'root','password')
// conn = Sql.newInstance(jdbcUrl,'root','password','com.mysql.jdbc.Driver')

def rb = new RuleBuilderImpl(conn,'core')

rb.addRule('error3',"""
require(trap)
""","need a category that does exist but is not in the input data", "Faucet")
System.exit(0)
rb.addRule('error1',"""
require(notexist)
""","we need a category that does not exist", "Faucet")
rb.addRule('error2',"""
sink.unknown_attribute = 42
""","we need a category that does not exist", "Sink")

rb.addRule('ats2',"""
require(drainage)
""","a sick must have drainage", "Sink")
rb.addRule('ats2',"""
sink.drainage_size = drainage.flange_size
""","the flage must fit the sink", "Sink","Drainage")
// facet


// faucet must have sink
// where
rb.addRule('ats2',"""
faucet.powered ? cable : true
""","if a faucet is powered, it must have a cable", "Faucet")
// facet

rb.addRule('ats1',"""
    switch(faucet.pipes)
       case 1: faucet.pipediameter < sink.holediameter OR faucet.pipeguage < sink.centreholediameter
       case 2:
       case 3:
""","Fit sink holes","Faucet","Sink")

//  if(faucet.pipes = 1) MATCH

//===========================
//  if(faucet.pipes > 1) 
rb.addRule('ats1',"""
  faucet.pipeoncentre = sink.holeoncentre
""","Match faucet pipe centre to sink pipes","Faucet","Sink")
//  if(faucet.pipeoncentre =(-/+ 1/4 inch) sink.holeoncentre
//===========================
rb.addRule('ats1',"""
((sink.holediameter - faucet.pipeguage) / 2) < abs(sink.holedistance-faucet.pipedistance)
""","Match fauct pipe diameter to sink pipes","Faucet","Sink")
//===========================

rb.addRule('ats1',"""
if(faucet.connection_type) {
	musthave (powercable)
}
""","Match fauct pipe diameter to sink pipes","Faucet","Sink")
//===========================

rb.addRule('ats1',"""
if(faucet.connection_type) {
	connectors(faucet.connection_type, powercable.connection) // connector wrong
	powercable.voltage = faucet.voltage
	powercable.amprs >= faucet.amps
	powercable.watts >= faucet.watts
}

""","Match fauct pipe diameter to sink pipes","Faucet")

//===========================
rb.addRule('ats1',"""
if(carrier.negative_power) {
    ! faucet.connection_type
}
""","Match fauct pipe diameter to sink pipes","Carriers")
//===========================

rb.addRule('ats1',"""
sink.drainagesize = drainage.flangesize (number + unit)
""","Match fauct pipe diameter to sink pipes","Sink","Drainage")
//===========================
