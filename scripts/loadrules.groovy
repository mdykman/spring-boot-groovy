import java.security.MessageDigest
import groovy.sql.Sql
import com.mysql.jdbc.*


import groovy.json.*
import groovy.util.*

@Grapes(
  @Grab(group='mysql', module='mysql-connector-java',  version='5.1.25')
)
@GrabConfig(systemClassLoader = true)

jdbcUrl = 'jdbc:mysql://localhost/ats_rules'

conn = Sql.newInstance(jdbcUrl,'root','password')
// conn = Sql.newInstance(jdbcUrl,'root','password','com.mysql.jdbc.Driver')

def rb = new RuleBuilder(conn)


// faucet must have sink
// where
rb.addRule('ats1',"""
if(sink) switch(faucet.pipes)
    case 1: sink.holes = 1 or sink.holes = 3
    case 2: sink.holes = 2
    case 3: sink.holes = 3
""","Match sink holes", "Faucet","Sink")
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
""","Match fauct pipe diameter to sink pipes","Carrier")
//===========================

rb.addRule('ats1',"""
sink.drainagesize = drainage.flangesize (number + unit)
""","Match fauct pipe diameter to sink pipes","Sink","Drainage")
//===========================
