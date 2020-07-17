#!/bin/bash

NUM=$1;
shift;


time for xx in `seq $NUM`; do  
	curl  --data @items3.json -H "Content-Type: application/json" http://localhost:8080/validate >/dev/null 2>&1; 
done; 
