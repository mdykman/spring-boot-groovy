#!/bin/bash

LOGFILE=./ats-rule-engine.log
kill `ps -ef | grep rule-engine | awk '$8 == "java" {print $2}'`


java -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Ddb.properties=db.properties -Druleset=ats2,ats3,error3,error4 -jar build/libs/ats-rule-engine.jar >> $LOGFILE 2>&1 &

