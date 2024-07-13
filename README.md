# redis4j

## Introduction

**redis4j** is a comprehensive library designed to support Spring Boot projects by simplifying Redis integration. This
library manages Redis connections and provides essential Redis services, including setting, getting, and removing keys.
Enhance your Spring Boot applications with ease using **redis4j**.

## Features

- Comprehensive set of utility functions.
- Written in Java 1.8.
- Well-documented code for easy understanding.
- Regular updates and maintenance.

## Installation

```bash
git clone --depth 1 https://github.com/sivaosorg/redis4j.git
```

## Generation Plugin Java

```bash
curl https://gradle-initializr.cleverapps.io/starter.zip -d type=groovy-gradle-plugin  -d testFramework=testng -d projectName=redis4j -o redis4j.zip
```

## Modules

Explain how users can interact with the various modules.

### Tidying up

To tidy up the project's Java modules, use the following command:

```bash
./gradlew clean
```

or

```bash
make clean
```

### Building SDK

```bash
./gradlew jar
```

or

```bash
make jar
```

### Upgrading version

- file `gradle.yml`

```yaml
ng:
  name: redis4j
  version: v1.0.0
  enabled_link: false # enable compression and attachment of the external libraries
  jars:
    - enabled: false # enable compression and attachment of the external libraries
      source: "./../libs/unify4j-v1.0.0.jar" # lib Jar
    - enabled: false
      source: ""
```

## Add dependencies

```groovy
// The "spring-data-redis" library, version 2.7.8, is a Spring Data module that provides easy configuration and access to Redis from Spring applications,
// offering comprehensive support for Redis operations, including connection management, RedisTemplate, and repository support for Spring Data.
implementation group: 'org.springframework.data', name: 'spring-data-redis', version: '2.7.8'
// The "spring-integration-redis" library, version 5.5.20, is a Spring Integration module that provides support for Redis-based messaging,
// enabling integration with Redis to send and receive messages, as well as leveraging Redis Pub/Sub capabilities within Spring applications.
// Using runtimeOnly to ensure this dependency is only included at runtime.
runtimeOnly group: 'org.springframework.integration', name: 'spring-integration-redis', version: '5.5.20'
// The "lettuce-core" library, version 6.2.3.RELEASE, is a powerful and thread-safe Redis client for Java,
// providing asynchronous, synchronous, and reactive API support to efficiently interact with Redis servers.
implementation group: 'io.lettuce', name: 'lettuce-core', version: '6.2.3.RELEASE'
// The "jedis" library, version 5.1.3, is a simple and feature-rich Java client for Redis,
// providing synchronous and asynchronous communication with Redis servers to perform various operations and transactions.
implementation group: 'redis.clients', name: 'jedis', version: '5.1.3'
```

## Integration

1. Add dependency into file `build.gradle`

```gradle
implementation files('libs/redis4j-v1.0.0.jar') // filename based on ng.name and ng.version
```

2. Edit file `main Spring Boot application` (optional)

```java

@SpringBootApplication
@ComponentScan(basePackages = {"org.redis4j"}) // root name of package wizard4j
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```
