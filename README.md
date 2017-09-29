**!!! This repository is not linked in any manner to Zinc.io or ZincAPI.com !!!**

# Abstract #

Basic server playing as a Fake Zinc.io server.

# Features #
This allows to get and post orders, without consuming the real API and therefore saving billing...

# Technical #
* based on Spring Boot, forked from https://github.com/spring-guides/gs-rest-service
* server: Undertow on localhost and port 9090 by default (can be configured in `config/application.yml`)
* build: ```mvn clean install```
* run  : ```mvn spring-boot:run```

# Needed #
The JAR fr.sayasoft:zinc-java-sdk:jar is needed. The source code for it is available in GitHub: 
https://github.com/JonathanLalou/zinc-java-sdk

# Behaviour and Rules #
## Creating (`POST`) an OrderRequest ##

* When posting an `OrderRequest` to the fake server, if the `idempotency` field of the `OrderRequest` is set a response is returned:
```$json
{ "request_id": "fakeRequestIdStart-<idempotency>-fakeRequestIdEnd"}
```
Eg: for an `itempotency` `helloWorld`, the returned response: 
```$json
{ "request_id": "fakeRequestIdStart-helloWorld-fakeRequestIdEnd"}
```
* if the posted `OrderRequest` contains a valid `ZincErrorCode` in the field `clientNotes`, then this error code is returned as response by the fake server.
* if the posted `OrderRequest` contains webhooks, then the list of webhooks will be looped on and the webhooks will be called, with a delay of 30 seconds between two calls.

## Retrieving (`GET`) an OrderRequest ##

* When getting an `OrderRequest` from the fake server, the returned response is always the same valid JSON String.

## Tip! ##
The fake server embeds webhooks, mapped on path `/webhook/{webhookType}/{requestId}`. They can be called, eg:
```
http://localhost:9090/webhook/statusUpdated/abcd
http://localhost:9090/webhook/requestSucceeded/abcd
```  
## Tip! ##
The fake server is mounted on SpringBoot and SpringBootActuator is enabled, with security level lowered. As a consequence, the fake server can be gracefully shutdown on posting a query to the endpoint `/shutdown`, eg:
```
curl -X POST http://localhost:9090/shutdown
```
Yet, SpringBootActuator features can be disabled in `config/application.yml` and/or in `pom.xml`