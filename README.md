**!!! This repository is not linked in any manner to Zinc.io or ZincAPI.com !!!**

# Abstract #

Basic server playing as a Fake Zinc.io server.

# Features #
This allows to get and post orders, without consuming the real API and therefore saving billing...

# Technical #
* based on Spring Boot, forked from https://github.com/spring-guides/gs-rest-service
* server: Undertow on localhost:9090
* build: ```mvn clean install```
* run  : ```mvn spring-boot:run```

# Needed #
The JAR fr.sayasoft:zinc-java-sdk:jar is needed. The source code for it is available in GitHub: 
https://github.com/JonathanLalou/zinc-java-sdk

# Shutdown #
SpringBoot Actuator is enabled, therefore health is monitored. Moreover, to shutdown gracefully (very useful during integration and non-regression tests), call ```/shutdown``` in POST, eg:

    curl -X POST http://localhost:9090/shutdown
