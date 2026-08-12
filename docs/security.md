# Security

## 1. Overview

The Employee Management application uses **Spring Security** to control access to application endpoints.

The security configuration follows a simple access model:

- Employee APIs are publicly accessible
- Account APIs are publicly accessible
- Actuator endpoints require authentication
- Actuator authentication uses HTTP Basic Authentication
- CSRF protection is disabled because the application exposes REST APIs
- Authentication is currently required only for `/actuator/**`

The security configuration is implemented using a `SecurityFilterChain`.

---

## 2. Security Dependency

Spring Security is included in the application to provide authentication and authorization support.

The main security configuration is located in:

```text
src/main/java/com/tns/empmanagement/config/SecurityConfig.java
```

---

## 3. Security Configuration

The application uses the following security configuration:

```java
package com.tns.empmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").authenticated()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
```

---

## 4. Access Rules

The application's security rules are:

```text
/actuator/**       -> Authentication required
Any other request  -> Public access
```

The important part of the configuration is:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").authenticated()
    .anyRequest().permitAll()
)
```

This means that only Actuator endpoints are protected by authentication.

---

## 5. Employee API Security

Employee APIs are publicly accessible.

The following endpoints do not require authentication:

```text
GET    /api/v1/employees
GET    /api/v1/employees/{id}
POST   /api/v1/employees
PUT    /api/v1/employees/{id}
DELETE /api/v1/employees/{id}
```

For example:

```text
GET http://localhost:8080/api/v1/employees
```

can be accessed without providing a username or password.

---

## 6. Account API Security

Account APIs are also publicly accessible.

The following endpoints do not require authentication:

```text
POST /api/v1/accounts/{id}/deposit
POST /api/v1/accounts/{id}/withdraw
GET  /api/v1/accounts/{id}/balance
```

For example:

```text
GET http://localhost:8080/api/v1/accounts/1/balance
```

can be accessed without authentication.

---

## 7. Actuator Security

Actuator endpoints are protected.

The security rule:

```java
.requestMatchers("/actuator/**").authenticated()
```

requires authentication for all Actuator endpoints.

Examples include:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

Therefore, requests to these endpoints must provide valid authentication credentials.

---

## 8. HTTP Basic Authentication

The application enables HTTP Basic Authentication using:

```java
.httpBasic(Customizer.withDefaults());
```

HTTP Basic Authentication sends credentials with the HTTP request.

The general request flow is:

```text
Client
   |
   | Username + Password
   v
Spring Security
   |
   +---- Valid ----> Actuator Endpoint
   |
   +---- Invalid --> 401 Unauthorized
```

---

## 9. Default Security User

The application defines a Spring Security user in `application.yml`.

The configuration is:

```yaml
spring:
  security:
    user:
      name: root
      password: <configured-password>
```

The username configured for the application is:

```text
root
```

The password is defined in the application's configuration.

For security reasons, passwords should not be committed directly to a public source-code repository.

---

## 10. Testing Protected Actuator APIs

Actuator endpoints can be tested using Postman.

Example:

```text
GET http://localhost:8080/actuator/health
```

In Postman, configure:

```text
Authorization
Type: Basic Auth
Username: root
Password: <configured-password>
```

Then send the request.

A valid authenticated request should receive:

```text
HTTP 200 OK
```

when the application is healthy.

---

## 11. Testing Actuator Without Authentication

If a protected Actuator endpoint is requested without valid authentication:

```text
GET http://localhost:8080/actuator/health
```

the request should not be treated as an authenticated request.

The expected result is generally:

```text
HTTP 401 Unauthorized
```

This confirms that Actuator endpoints are protected.

---

## 12. Testing Public Employee APIs

Employee APIs do not require authentication.

For example:

```text
GET http://localhost:8080/api/v1/employees
```

No Authorization header is required.

Expected response:

```text
HTTP 200 OK
```

when the request is successful.

---

## 13. Testing Public Account APIs

Account APIs also do not require authentication.

For example:

```text
GET http://localhost:8080/api/v1/accounts/1/balance
```

No Authorization header is required.

Expected response:

```text
HTTP 200 OK
```

when the account exists and the request is successful.

---

## 14. CSRF Configuration

CSRF protection is disabled:

```java
.csrf(csrf -> csrf.disable())
```

This is appropriate for the current REST API design because the application is not using a traditional browser-based session workflow.

The APIs are accessed using HTTP requests such as:

```text
GET
POST
PUT
DELETE
```

and authentication for the protected Actuator endpoints uses HTTP Basic Authentication.

---

## 15. Why CSRF Is Disabled

CSRF protection is primarily designed to protect browser-based applications that use cookies and session-based authentication.

This application exposes REST APIs and does not use a traditional form-login session model.

Therefore, the current configuration disables CSRF:

```java
.csrf(csrf -> csrf.disable())
```

If the application's authentication architecture changes in the future, the CSRF configuration should be reviewed accordingly.

---

## 16. Authorization Model

The current application has a simple authorization model.

```text
                    Spring Security
                          |
             +------------+------------+
             |                         |
             v                         v
       /actuator/**                Other APIs
             |                         |
             v                         v
      Authentication              Public Access
          Required
             |
       +-----+-----+
       |           |
     Valid       Invalid
       |           |
       v           v
    Access       401
```

This keeps business APIs public while protecting operational and monitoring endpoints.

---

## 17. Security of Employee Operations

The Employee API supports:

```text
Create Employee
Read Employees
Read Employee
Update Employee
Delete Employee
```

These operations are currently public because the security configuration uses:

```java
.anyRequest().permitAll()
```

No username, password, token, or Authorization header is required for these operations.

---

## 18. Security of Banking Operations

The Account API supports:

```text
Deposit
Withdraw
Get Balance
```

These operations are also publicly accessible under the current configuration.

For example:

```text
POST /api/v1/accounts/{id}/deposit
POST /api/v1/accounts/{id}/withdraw
GET  /api/v1/accounts/{id}/balance
```

No authentication is currently required for these endpoints.

---

## 19. Postman Authentication

The Postman collection contains Employee and Account API requests.

Because these APIs are public, authentication is not required for them.

For protected Actuator requests, Basic Authentication should be configured.

Example:

```text
Authorization Type: Basic Auth
Username: root
Password: <configured-password>
```

---

## 20. Newman Testing

The Employee and Account APIs can be executed using Newman without authentication because they are publicly accessible.

Example:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

The collection can contain:

```text
Employee
    |
    +-- Get all Employees
    +-- Get Employee by ID
    +-- Add Employee
    +-- Update Employee
    +-- Delete Employee

Account
    |
    +-- Deposit Amount
    +-- Withdraw Amount
    +-- Get Balance
```

These requests do not need Basic Authentication.

---

## 21. Postman Pre-request Script

The Postman collection contains a collection-level pre-request script.

The current script is:

```javascript
pm.environment.set("token", "token-value");

pm.request.headers.add({
    key: "Authorization",
    value: "Bearer " + pm.environment.get("token")
});
```

This script adds a Bearer token to requests.

However, the current Spring Security configuration does **not** use Bearer token authentication.

The current application uses:

```java
.httpBasic(Customizer.withDefaults());
```

Therefore, the Bearer token is not required for the Employee and Account APIs and is not the authentication mechanism used by the current application.

---

## 22. Current Authentication vs Postman Token

There is an important difference between the current application security configuration and the Postman collection.

Application:

```text
Spring Security
      |
      v
HTTP Basic Authentication
      |
      v
Actuator endpoints
```

Postman collection pre-request script:

```text
Bearer Token
      |
      v
Authorization Header
```

The current Spring Security configuration does not configure OAuth2/JWT/Bearer authentication.

Therefore, the `token` environment variable is not needed for the current application's security implementation.

If the Postman collection is intended to test only the currently implemented security configuration, the unnecessary Bearer token logic can be removed or replaced with Basic Authentication where required.

---

## 23. Basic Authentication in Postman

For Actuator requests, Basic Authentication can be configured directly in Postman.

Example:

```text
Request:
GET http://localhost:8080/actuator/health

Authorization:
Basic Auth

Username:
root

Password:
<configured-password>
```

This matches:

```java
.httpBasic(Customizer.withDefaults());
```

---

## 24. Security Testing Scenarios

The following security scenarios can be tested.

### Scenario 1: Employee API Without Authentication

Request:

```text
GET /api/v1/employees
```

Expected:

```text
200 OK
```

---

### Scenario 2: Account API Without Authentication

Request:

```text
GET /api/v1/accounts/1/balance
```

Expected:

```text
200 OK
```

when the account exists.

---

### Scenario 3: Actuator With Valid Authentication

Request:

```text
GET /actuator/health
```

with valid Basic Authentication.

Expected:

```text
200 OK
```

when the application is healthy.

---

### Scenario 4: Actuator Without Authentication

Request:

```text
GET /actuator/health
```

without credentials.

Expected:

```text
401 Unauthorized
```

---

### Scenario 5: Actuator With Invalid Credentials

Request:

```text
GET /actuator/health
```

with incorrect credentials.

Expected:

```text
401 Unauthorized
```

---

## 25. Security Testing Matrix

| API Category | Authentication | Expected Result |
|---|---|---|
| Employee API | Not required | Public access |
| Account API | Not required | Public access |
| Actuator API | Basic Auth required | Authenticated access |
| Actuator without credentials | Required | 401 Unauthorized |
| Actuator with invalid credentials | Required | 401 Unauthorized |
| Actuator with valid credentials | Basic Auth | Access granted |

---

## 26. Security Configuration Flow

The complete request authorization flow is:

```text
HTTP Request
     |
     v
Spring Security Filter Chain
     |
     v
Is URL /actuator/** ?
     |
     +--------------------+
     |                    |
    Yes                   No
     |                    |
     v                    v
Authentication       permitAll()
 Required                 |
     |                     |
     +--------+------------+
              |
              v
          Controller
```

---

## 27. Protected Management Endpoints

The application exposes several Actuator endpoints:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

Because the security rule is:

```java
.requestMatchers("/actuator/**").authenticated()
```

all of these endpoints require authentication.

---

## 28. Why Actuator Is Protected

Actuator endpoints can expose operational information about the application.

For example:

```text
/actuator/env
/actuator/beans
/actuator/mappings
/actuator/metrics
```

may reveal information about:

- Application configuration
- Spring beans
- Registered endpoints
- Runtime metrics
- Environment properties
- Application internals

Therefore, these endpoints should not be publicly accessible.

The application protects them using Spring Security.

---

## 29. Security and Monitoring

Spring Security and Actuator work together as follows:

```text
Monitoring Client
       |
       v
/actuator/health
       |
       v
Spring Security
       |
       +---- Valid Basic Auth ----> Actuator
       |
       +---- Invalid/Missing -----> 401
```

This allows monitoring endpoints to remain protected while still being available to authorized users and monitoring systems.

---

## 30. Security and REST APIs

The application follows a simple REST API security approach.

Business APIs:

```text
/api/v1/employees/**
/api/v1/accounts/**
```

are currently public.

Management APIs:

```text
/actuator/**
```

are protected.

This separation makes the security configuration easy to understand and maintain.

---

## 31. Current Security Limitations

The current security configuration is intentionally simple.

The following are not currently implemented:

- JWT authentication
- OAuth2 authentication
- Role-based authorization
- Permission-based authorization
- User registration
- Database-backed user authentication
- Refresh tokens
- API key authentication

The current implementation focuses on protecting Actuator endpoints using HTTP Basic Authentication.

---

## 32. Production Security Considerations

For a production application, additional security measures should be considered.

These include:

- Use HTTPS
- Do not hard-code passwords
- Store secrets using environment variables or a secrets manager
- Use strong passwords
- Consider role-based authorization
- Protect sensitive business APIs
- Consider JWT/OAuth2 for distributed applications
- Restrict Actuator endpoints
- Expose only required Actuator endpoints
- Avoid exposing sensitive environment information
- Configure appropriate CORS policies
- Review CSRF requirements when authentication architecture changes
- Apply rate limiting where appropriate
- Audit security-related events

---

## 33. Password Management

The development configuration contains a Spring Security username and password.

For example:

```yaml
spring:
  security:
    user:
      name: root
      password: <configured-password>
```

Credentials should not be committed as plain text in a public repository.

A production environment should use environment variables or a secure secrets-management mechanism.

For example:

```yaml
spring:
  security:
    user:
      name: ${ACTUATOR_USERNAME}
      password: ${ACTUATOR_PASSWORD}
```

The actual credentials can then be provided through the deployment environment.

---

## 34. Security Configuration Summary

The application's current security configuration can be summarized as:

```text
Spring Security
      |
      +-- CSRF
      |     |
      |     +-- Disabled
      |
      +-- Employee APIs
      |     |
      |     +-- Public
      |
      +-- Account APIs
      |     |
      |     +-- Public
      |
      +-- Actuator APIs
            |
            +-- Authentication Required
            |
            +-- HTTP Basic Authentication
```

---

## 35. Files Related to Security

The main security-related files are:

```text
src/
└── main/
    ├── java/
    │   └── com/tns/empmanagement/
    │       └── config/
    │           └── SecurityConfig.java
    │
    └── resources/
        └── application.yml
```

`SecurityConfig.java` contains the Spring Security filter-chain configuration.

`application.yml` contains the configured Spring Security user and password.

---

## 36. Final Security Summary

The Employee Management application uses Spring Security with HTTP Basic Authentication.

The current security policy is:

```text
Employee APIs  -> Public
Account APIs   -> Public
Actuator APIs  -> Basic Authentication
```

The main authorization configuration is:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").authenticated()
    .anyRequest().permitAll()
)
```

HTTP Basic Authentication is enabled using:

```java
.httpBasic(Customizer.withDefaults());
```

CSRF is disabled for the REST API configuration:

```java
.csrf(csrf -> csrf.disable())
```

This provides a straightforward security setup where business APIs remain publicly accessible as required, while application monitoring and management endpoints are protected from unauthenticated access.