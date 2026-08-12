# Actuator Monitoring

## 1. Overview

The Employee Management application uses **Spring Boot Actuator** to monitor and inspect the running application.

Actuator provides endpoints for:

- Application health
- Application metrics
- Cache information
- Application mappings
- Spring beans
- Environment information
- Application information
- Kubernetes liveness and readiness probes
- JVM and system metrics

Actuator endpoints are not intended to be publicly accessible and are protected using Spring Security.

---

## 2. Spring Boot Actuator Dependency

Spring Boot Actuator is added as a project dependency.

Maven dependency:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

This enables the Actuator endpoints provided by Spring Boot.

---

## 3. Actuator Base URL

The application runs locally on:

```text
http://localhost:8080
```

Actuator endpoints are available under:

```text
/actuator
```

Therefore, the base Actuator URL is:

```text
http://localhost:8080/actuator
```

---

## 4. Exposed Actuator Endpoints

The application exposes the following Actuator endpoints through `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,mappings,caches,info,beans,env
```

The exposed endpoints include:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

Only the endpoints explicitly included in the configuration are exposed over HTTP.

---

## 5. Health Endpoint

The health endpoint is:

```text
GET /actuator/health
```

Full URL:

```text
http://localhost:8080/actuator/health
```

It provides information about the health of the application and its configured components.

The endpoint is configured with:

```yaml
management:
  endpoint:
    health:
      show-details: always
```

This allows health details to be displayed when the endpoint is successfully accessed.

---

## 6. Health Configuration

The relevant configuration is:

```yaml
management:
  endpoint:
    health:
      show-details: always
      probes:
        enabled: true
```

This enables health information and health probes.

Health information can be useful for checking whether the application and its dependencies are functioning correctly.

---

## 7. Liveness Probe

Liveness indicates whether the application itself is running.

The application enables the liveness state:

```yaml
management:
  health:
    livenessstate:
      enabled: true
```

The liveness endpoint can be accessed through:

```text
/actuator/health/liveness
```

Full URL:

```text
http://localhost:8080/actuator/health/liveness
```

A healthy application should report a successful liveness status.

---

## 8. Readiness Probe

Readiness indicates whether the application is ready to receive traffic.

The application enables the readiness state:

```yaml
management:
  health:
    readinessstate:
      enabled: true
```

The readiness endpoint is:

```text
/actuator/health/readiness
```

Full URL:

```text
http://localhost:8080/actuator/health/readiness
```

This endpoint can be used by orchestration systems such as Kubernetes to determine whether the application is ready to receive requests.

---

## 9. Kubernetes Health Probes

Liveness and readiness probes are useful when the application is deployed in Kubernetes.

The general flow is:

```text
Kubernetes
     |
     +---- Liveness Probe
     |          |
     |          v
     |     Application Alive?
     |
     +---- Readiness Probe
                |
                v
          Application Ready?
```

If the application is not live, Kubernetes can restart the container.

If the application is not ready, Kubernetes can temporarily stop routing traffic to it.

---

## 10. Metrics Endpoint

The metrics endpoint is:

```text
GET /actuator/metrics
```

Full URL:

```text
http://localhost:8080/actuator/metrics
```

It provides a list of available application metrics.

Individual metrics can be requested using:

```text
/actuator/metrics/{metricName}
```

For example:

```text
/actuator/metrics/jvm.memory.used
```

The metrics endpoint is useful for observing application and JVM behavior.

---

## 11. JVM Metrics

Spring Boot Actuator provides JVM-related metrics automatically.

Examples include:

```text
jvm.memory.used
jvm.memory.committed
jvm.memory.max
jvm.gc.pause
jvm.threads.live
jvm.threads.daemon
jvm.threads.peak
```

These metrics help monitor:

- Heap memory
- Garbage collection
- JVM threads
- Memory allocation
- JVM resource usage

---

## 12. Cache Metrics

The application uses Spring Cache with Redis.

Cache metrics are enabled through:

```yaml
management:
  metrics:
    enable:
      cache: true
```

This allows cache-related metrics to be monitored through Actuator.

The application also exposes:

```text
/actuator/caches
```

which provides information about configured caches.

---

## 13. Cache Monitoring

Cache monitoring is useful for understanding whether cached data is being used effectively.

Important concepts include:

```text
Cache hits
Cache misses
Cache size
Cache usage
```

The application can use Actuator metrics together with Redis monitoring to understand cache behavior.

---

## 14. Mappings Endpoint

The mappings endpoint is:

```text
GET /actuator/mappings
```

Full URL:

```text
http://localhost:8080/actuator/mappings
```

It provides information about the request mappings registered in the Spring application.

This can help during development and debugging by showing which controllers and endpoints are registered.

---

## 15. Beans Endpoint

The beans endpoint is:

```text
GET /actuator/beans
```

Full URL:

```text
http://localhost:8080/actuator/beans
```

It provides information about Spring beans managed by the application context.

This can be useful for debugging dependency injection and application configuration.

---

## 16. Environment Endpoint

The environment endpoint is:

```text
GET /actuator/env
```

Full URL:

```text
http://localhost:8080/actuator/env
```

It provides information about the application's environment and configuration properties.

Because environment information can contain sensitive configuration data, access to this endpoint should be restricted.

The application protects Actuator endpoints using Spring Security.

---

## 17. Info Endpoint

The info endpoint is:

```text
GET /actuator/info
```

Full URL:

```text
http://localhost:8080/actuator/info
```

The application enables environment information using:

```yaml
management:
  info:
    env:
      enabled: true
```

This allows configured environment information to be included in the Actuator information endpoint.

---

## 18. Security of Actuator Endpoints

The application uses Spring Security to protect Actuator endpoints.

The security configuration contains:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").authenticated()
    .anyRequest().permitAll()
)
.httpBasic(Customizer.withDefaults());
```

This means:

```text
/actuator/**       -> Authentication required
/api/v1/**         -> Public access
Other endpoints    -> Public access unless otherwise configured
```

Therefore, Actuator endpoints are not publicly accessible.

---

## 19. HTTP Basic Authentication

Actuator endpoints use HTTP Basic Authentication.

The configured Spring Security user is:

```yaml
spring:
  security:
    user:
      name: root
      password: <configured-password>
```

The actual password should not be committed to source control in a production environment.

When accessing a protected Actuator endpoint from Postman, select:

```text
Authorization
    |
    +-- Type: Basic Auth
    +-- Username: root
    +-- Password: <configured-password>
```

---

## 20. Testing Actuator Health

The health endpoint can be tested using Postman.

Request:

```text
GET http://localhost:8080/actuator/health
```

Because the endpoint is protected, Basic Authentication must be supplied.

Expected result for a healthy application:

```text
HTTP 200 OK
```

The response contains health information indicating whether the application is healthy.

---

## 21. Testing Liveness

Request:

```text
GET http://localhost:8080/actuator/health/liveness
```

Authentication is required according to the application's security configuration.

Expected result:

```text
HTTP 200 OK
```

when the application is alive.

---

## 22. Testing Readiness

Request:

```text
GET http://localhost:8080/actuator/health/readiness
```

Expected result:

```text
HTTP 200 OK
```

when the application is ready.

This endpoint is particularly useful for containerized and Kubernetes deployments.

---

## 23. Testing Metrics

Request:

```text
GET http://localhost:8080/actuator/metrics
```

The response contains the available metrics.

A specific metric can then be requested.

For example:

```text
GET http://localhost:8080/actuator/metrics/jvm.memory.used
```

This returns information about JVM memory usage.

---

## 24. Monitoring DataSource and HikariCP

The application uses HikariCP for database connection pooling.

Actuator can expose metrics related to the DataSource and connection pool when the required metrics are available.

These metrics can be used to monitor:

```text
Active connections
Idle connections
Maximum connections
Minimum connections
Connection usage
```

This helps identify database connection pool pressure and possible pool exhaustion.

---

## 25. HikariCP Configuration

The application configures HikariCP with:

```yaml
spring:
  datasource:
    hikari:
      pool-name: EmployeePool
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      max-lifetime: 1800000
      idle-timeout: 600000
```

The configuration means:

```text
Pool name          = EmployeePool
Maximum pool size  = 10
Minimum idle       = 5
Connection timeout = 30 seconds
Max lifetime       = 30 minutes
Idle timeout       = 10 minutes
```

These settings control how database connections are managed.

---

## 26. Monitoring Redis and Cache Usage

The application uses Redis as its cache provider.

Redis is running through a Docker container.

The configured Redis connection is:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60s
```

Actuator cache information and metrics can be used together with Redis monitoring to observe cache behavior.

---

## 27. Application Monitoring Flow

The overall monitoring architecture is:

```text
Client / Monitoring Tool
          |
          v
Spring Boot Actuator
          |
          +------------------+
          |                  |
          v                  v
      Health              Metrics
          |                  |
          v                  v
 Database / Redis       JVM / Cache /
 Application            DataSource
```

Actuator provides a common monitoring interface for the Spring Boot application.

---

## 28. Actuator and Application APIs

The normal application APIs and Actuator APIs serve different purposes.

Application APIs:

```text
/api/v1/employees
/api/v1/employees/{id}
/api/v1/accounts/{id}/deposit
/api/v1/accounts/{id}/withdraw
/api/v1/accounts/{id}/balance
```

Actuator APIs:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

The application APIs provide business functionality.

Actuator provides operational and monitoring information.

---

## 29. Local Monitoring Workflow

The local monitoring workflow is:

```text
Start SQL Server
       |
       v
Start Redis Docker Container
       |
       v
Start Spring Boot Application
       |
       v
Application Starts
       |
       v
Actuator Endpoints Available
       |
       +---- Health
       |
       +---- Metrics
       |
       +---- Cache Information
       |
       +---- JVM Metrics
       |
       +---- Application Information
```

---

## 30. Monitoring Through Postman

The Actuator endpoints can also be tested using Postman.

Example request:

```text
GET http://localhost:8080/actuator/health
```

Configure:

```text
Authorization Type: Basic Auth
Username: root
Password: <configured-password>
```

Then send the request.

The same approach can be used for other protected Actuator endpoints.

---

## 31. Monitoring Through Newman

Actuator requests can also be added to a Postman collection and executed using Newman.

For example:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

If protected Actuator requests are included in the collection, their authentication must also be configured correctly.

The existing Employee and Account requests do not require authentication because they are public according to the application's Spring Security configuration.

---

## 32. Production Security Considerations

Actuator endpoints can expose sensitive operational information.

In particular:

```text
/actuator/env
/actuator/beans
/actuator/mappings
```

may expose information that should not be publicly available.

For production deployments:

- Protect Actuator endpoints
- Use strong credentials
- Avoid hard-coded passwords
- Store secrets securely
- Restrict network access
- Expose only required endpoints
- Avoid exposing sensitive environment information
- Monitor access to management endpoints

The current project protects the Actuator endpoints using Spring Security and HTTP Basic Authentication.

---

## 33. Configured Application Monitoring

The relevant `application.yml` monitoring configuration is:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,mappings,caches,info,beans,env

  endpoint:
    health:
      show-details: always
      probes:
        enabled: true

  health:
    livenessstate:
      enabled: true
    readinessstate:
      enabled: true

  info:
    env:
      enabled: true

  metrics:
    enable:
      cache: true
```

This configuration enables the monitoring features used by the application.

---

## 34. Monitoring Objectives

The Actuator implementation provides monitoring capabilities for:

- Application health
- Liveness
- Readiness
- JVM memory
- Garbage collection
- JVM threads
- Cache metrics
- Redis-backed caching
- DataSource and HikariCP metrics
- Application mappings
- Spring beans
- Environment information
- Application information

These capabilities make it easier to identify runtime problems and monitor application behavior.

---

## 35. Summary

Spring Boot Actuator is integrated into the Employee Management application to provide operational monitoring and health information.

The application exposes selected Actuator endpoints:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

Liveness and readiness probes are enabled for container and Kubernetes deployments.

JVM, cache, and DataSource-related metrics can be monitored through Actuator.

Most importantly, Actuator endpoints are protected using Spring Security and HTTP Basic Authentication, while the Employee and Account business APIs remain publicly accessible according to the current security configuration.

The monitoring setup provides a foundation for observing application health, performance, caching, database connections, and runtime behavior.