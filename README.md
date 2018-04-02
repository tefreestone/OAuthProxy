# Legacy OAuth Reverse Proxy version 1.0.0

This project implements a [reverse proxy](https://en.wikipedia.org/wiki/Reverse_proxy) which retrieves authenticated resources for a client from an application.   This reverse proxy authenticates a user via oAuth 2.0 and forwards headers (if necessary) to an application.  This reverse proxy can forward requests to an external or docker co-located application.
The use case for this project is a legacy application that is too difficult to migrate to oAuth natively without a considerable effort.  This project provides an path to authenticate an HTTP Headers-based application without considerable effort.


#### Please Note:
The current version of this OAuth Legacy Reverse Proxy **DOES NOT** implement course grain URL pattern-based authorization policies (e.g. Exposee Oracle policies).  Contact the WAM team for WAM Oracle policy migration questions.  

This proxy uses Java 9.  Your legacy application may need some refactoring for the transition to Java 9.  

# Configuration
## Docker 
This docker image contains:
* CentOS 7.4
* Java 9
* .Net 2.0
* Apache Tomcat 7.0.76

## Zuul

This project is a [Spring Boot](https://projects.spring.io/spring-boot/) application which uses [Spring Cloud](https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html) [Zuul](https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html) to implement a reverse proxy.  Zuul is configured via an application.yml or application.properties.  The following is a sample application.yml:

```yaml
logging:

  level:

    # Only print warnings and above from external libraries

    org: INFO

    org.springframework: INFO

    # Only print informational messages and above from application code

    com.netflix.zuul: DEBUG

    org.springframework.cloud: DEBUG

security:

  basic:

    enabled: false

  oauth2:

    client:

      client-id: "{cipher}YouShouldEncryptClientId"

      client-secret: "{cipher}}YouShouldEncryptClientSecret"

      access-token-uri: "https://somehost.somedomain.org:443/sso/oauth2/access\_token"

      user-authorization-uri: "https:// somehost.somedomain.org:443/sso/oauth2/authorize"

      scope: "profile"

      client-authentication-scheme: "form"

      use-current-uri: true

    resource:

      user-info-uri: "https:// somehost.somedomain.org:443/sso/oauth2/userinfo"

      prefer-token-info: false

zuul:

  ignoredPatterns: /manage/\*\*

  routes:

    root:

      path: /\*\*

      url: http://localhost:8080

      strip-prefix: true
```


Zuul can be [configured](http://www.baeldung.com/spring-rest-with-zuul-proxy) using yml.  For more flexible configuration, this reverse proxy uses a a [Spring Cloud Config Server](https://cloud.spring.io/spring-cloud-config/single/spring-cloud-config.html).  The cloud config server allows you to version control forwarding rules in git. 



## Headers

Headers are retrieved with an implementation of HeadersService.   The default implementation returns an empty header map.  Custom HeaderService implementations override the default implementation.

# Running

## Spring Boot

Build Project
```bash
mvn clean install
```
Run executable jar
```bash
/usr/bin/java -Djava.security.egd=file:/dev/./urandom \

              -Dspring.profiles.active= whatever profile  \

              -jar ./oAuthProxy.jar
```

## Docker

This project has a Dockerfile and builds a docker image.  Currently, the dockerfile uses Java 9.  The version of Java can be specified in the dockerfile.  Configuration is located at /opt/oAuthProxy/config.  By default, the proxy is configured to use a Spring Cloud Config server.  A bootstrap.yml provides the necessary configuration (credentials, url etc) to use a Spring Cloud Config server.  If you wish to use a traditional application.yml instead of a Spring Cloud Config server, put the application.yml in the /opt/oAuthProxy/config.

Running image locally :
```bash
docker run -p 80:80 -it tefreestone/demo:1.0.0-SNAPSHOT
```

### ICS Implementation
Please refer to [Getting Started with Docker on Cloud Foundry](https://ip.ldschurch.org/578/document/using-docker-with-cloud-foundry) for information on deploying a docker image to our Cloud Foundry implemenation.