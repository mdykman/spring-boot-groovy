#!/bin/bash

## database properties

BASE=/usr/local/ats-rules
JARFILE="$BASE/ats-rule-engine.jar"

DBFILE="$BASE/db.properties"

RULESET="ats2,ats3"

JAVA=`which java`

$JAVA -Ddb.properties="$DBFILE" -Druleset="$RULESET" $JARFILE >> /var/log/ats-rule-engine.log 2>&1 &

