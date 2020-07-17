# ats-rule-engine

# Phases

The current project is curently envisioned as 2 broad phases.
* drawing validation
* drawing compostion

Each of the phases will have a final deliverable product consisting of one of more web services with correstpoding command line tools.

## Technology Choices
For each of these phases, webservices will be implemented and delivered as [spring-boot](https://projects.spring.io/spring-boot/) applications implemented in the [groovy programing language](http://groovy-lang.org/). Command line application will be imlemented in groovy.

Groovy allows for rapid development providing a terse, highly expressive, customizable language which is tightly bound with the JVM and has full access to all Java's extensive collection of libraries

As the delivered products are all implemented as services, it is expected that they will be deployed to unxi-like server class machine based on either linux or OS/X.  Deployment guarantees are currently constrained to those environments.  While it is expected that the delivered product will be able to run under Windows, if so required, that specific configuration is not one which is regularily tested in the course of development.

The language for rule specification will be a custom Domain Specific Language (DSL) based on groovy, selected for it's power of expession and user-friendliness

## Security constraints
It is assumed that security considerations are to be repected insofar as delivered webservices will be expected to integrate with ATS's existing infrastructure.  
## spring-boot
Spring Boot allows us to implement web services as robust stand-alone binaries.


## install sdkman and all requisite tools

**This section is of interest to developers only and may be ignored for deployment considerations.**

visit [SDKMON](https://sdkman.io) for details about this impressive tool.

Recommended for common Unix: linux, OS/X, cygwin, FreeBSD, Solaris

Install the command _sdk_

### quick method
```
curl -s "https://get.sdkman.io" | bash 
```

### careful method
```
curl -s "https://get.sdkman.io" > donottrust 
## check for NSA footprint
less donottrust
## if satisfied
cat donottrust | bash
```
You may need to reload or restart your shell to make the _sdk_ command available

These commands all play nicely with the system's update-alternatives module
```

sdk install java
sdk install groovy
sdk install maven
sdk install gradle
sdk install springboot
```

## Building
The only prerequisites to a successful build are 
 * availability of a Java 8 SDK
 * network access to jcentral, a maven repository from which all dependencies will be loaded. 
 
Explicit dependencies are listed in _build.gradle_ . The full list of required dependencies can be obtained with the command ` ./gradlew dependencies `

To build the application: 
```
./gradlew build
```

This will produce an executeable jar file, **build/libs/ats-rule-engine.jar** containing the application, embedded Tomcat and all dependencies. The jar is fully relocatable and may be run on any system with a Java 8 SDK.

The schema provided in schema/schema.sql must be loaded onto a MySQL 5.6+ database and a user created with READ/WRITE access to that database.

Database connection information must be entered into a properties file, the path of which is passed to the application at runtime.

contents of mydb.properties
```
username=ruleengine
password=mypassword
database=ats_rules
host=localhost
```

The name of the database configuration file is passed as a system property: ie. ` -Ddb.properties=mydb.properties`

If the property `db.properties` is not defined, the rule engine will atempt to obtain connection information directly from system properties with the following property names:

 * db.username
 * db.password
 * db.database
 * db.host

ie.

```
java -Ddb.host=localhost -Ddb.username=ruleengine -Ddb.password=mypassword -Ddb.database=ats_rules -jar ats-rule-engine.jar 
```

The ruleset(s) to be used by the application may also be specified as a system property: ie. ` -Druleset=ats2,ats3,experiment`

If no ruleset is specified for the application, the ruleset named **core** will be loaded


It should be noted that, until that schema is populated with relevant information, the application does nothing vrey interesting at all.


## Running
The runtime host must be provided with a Java 8 SDK.  It was developed and tested under OpenJDK but any vendor should be sufficient.

### Note 
A JRE is not an SDK.  The Java SDK/JDK is a runtime requirement for Tomcat (or any other servlet container) and groovy, in which language most of this application is written.  Please check that you have a Java SDK installed.

## Configuring
This will launch the application with the combined rulesets ats2 and ats3, using mydb.properties to specify database connection information
```
java -Ddb.properties=mydb.properties -Druleset=ats2,ats3 -jar ats-rule-engine.jar 
```

Like most typical spring-boot webservice applications, the application is intended to be shut down via the bullet-to-the-head method.

Any method preferred by the operator may be employed as the service is essentially a read-only service and will not leave broken data as a result of an abort.

For example, the application can be shutdown with a **hup**  signal or merely by shuting down the host container or host on which it is running.

It is expected that in production operation, multiple instances of the webservice could be launched with a proxy (ie. nginx) acting as a pool manager/front-end.  

A given instance would be launched before being added to the service pool and removed from the pool before it's instance is shutdown.  This should ensure uninterrupted operation.

## Endpoints

All output from this service are type application/json.  
All POST/PUT contents are also type application/json

 * POST    /validate    ## a json list of products per items3.json
 * GET     /categories  ## show all available categories
 * GET     /reload      ## refresh the rules from the database
 * GET     /rule        ## show all rules loaded in the service
 * PUT     /rule/{uuid}       ## modify an existing rule identified by UUID (not implemented)
 * POST    /rule        ## create a rule 
 * DELETE  /rule        ## delete a rule (not implemented)
 * GET     /rule/{category} ## show rules associated with a category
 * GET     /rule/{uuid} ## show a specific rule by idenified by UUID

## Payloads
 ### POST    /validate   
 The payload is an array of Products to be evaluated
  
  ```
  [ product (, ...)]
  ```
where a product is defined as an object having required properties
  
  * product_id - integer
  * category - integer
  * attributes - an object containing named attributes
 
 An attributes object typically contains one or more named objects to describe a single quality of a product.
 
 When an attribute is references in a script, it is the *value* field which is evaluated. *name*,*description*,*unit* are all optional.
 ```
     ...
    "attributes": {
      "powered": {
         "name": "powered",
         "description": "Is Electrically Powered",
         "value": true,
         "unit": null
      }
 ``` 
 Other properties may optionally be present on the Product object and may be referenced by rules if so required.
  
  A partial example:
 ```
 [{
   "product_id": 1,
   "name": "Faucet 1",
   "description": "Test Faucet",
   "date_added": "today",
   "make": "Moen",
   "model": "T44s",
   "factory_id": "Mt44s",
   "brand": "Moen",
   "category": 1,
   "attributes": {
      "powered": {
         "name": "powered",
         "description": "Is Electrically Powered",
         "value": true,
         "unit": null
      },
      "power-voltage": {
         "name": "voltage",
         "description": "Voltage",
         "value": 12,
         "unit": null
      },
      "power-ampheres": {
         "name": "amps",
         "description": "amps",
         "value": 0.5,
         "unit": null
 ...
 ```
The file *items3.json* represents the best complete example.

 ### POST    /rule        ## create a rule 
 
 To create a new rule, the payload is as specified below.
 
 The specified categories to related the rule to must already exist.
 
```
{
   "rule": {
      "description": "wall mounted sinks need carriers",
      "ruleGroup": "vb1",
      "weight": 1,
      "expr": "if (sink.mounting == 'wall'){\r\n\trequire {carrier}\r\n}\r\nreturn true"
   },
   "categories": [ "sink" ]
}
```
 ### POST    /categories        ## create one or more categories 
 
 To create one or more categories, the payload is as specified below.
 
 The specified categories must not already exist.
 
```
[ 'sink','faucet']
```

### PUT /rule/{uuid} ## update an existing rule
The specified categories to related the rule to must already exist.
```
{
   "rule": {
      "description": "wall mounted sinks need carriers",
      "ruleGroup": "vb1",
      "weight": 1,
      "expr": "if (sink.mounting == 'wall'){\r\n\trequire {carrier}\r\n}\r\nreturn true"
   },
   "categories": [ "sink" ]
}
```

## JMX

All implemented endpoints publish detailed timing information as JMX mBeans
