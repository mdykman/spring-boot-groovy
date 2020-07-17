#!/bin/bash

SCRIPT=$1;
shift;


cat /dev/stdin | groovy -cp ../bin:.:../builtin:../lib/mysql-connector-java-5.1.25.jar "$SCRIPT"

