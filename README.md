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
