# Employee Management & Banking API

A Spring Boot REST API application that provides Employee Management and Banking operations.

The application demonstrates REST API development, validation, global exception handling, SQL Server database integration, HikariCP connection pooling, Redis caching, Spring Security, Spring Boot Actuator, Postman API testing, and Newman CLI-based automated API test execution.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [API Overview](#api-overview)
- [Banking APIs](#banking-apis)
- [Employee APIs](#employee-apis)
- [Validation and Error Handling](#validation-and-error-handling)
- [Database](#database)
- [HikariCP Connection Pool](#hikaricp-connection-pool)
- [Redis Caching](#redis-caching)
- [Spring Security](#spring-security)
- [Spring Boot Actuator](#spring-boot-actuator)
- [API Testing with Postman](#api-testing-with-postman)
- [Running Tests with Newman](#running-tests-with-newman)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Documentation](#documentation)
- [Future Improvements](#future-improvements)

---

# Overview

The Employee Management & Banking API is a Spring Boot application developed to demonstrate backend REST API development and supporting production-oriented features.

The application contains two main functional modules:

1. Employee Management
2. Banking / Account Management

The project also includes:

- Request validation
- Global exception handling
- Standardized error responses
- SQL Server database persistence
- HikariCP connection pooling
- Redis caching
- Docker-based Redis
- Spring Security
- Spring Boot Actuator
- Postman collection-based API testing
- Newman CLI automated API test execution

---

# Features

## Employee Management

The Employee module provides APIs to:

- Create an employee
- Retrieve all employees with pagination
- Retrieve an employee by ID
- Update an employee
- Delete an employee
- Validate employee input
- Prevent duplicate employee email addresses

## Banking

The Banking module provides APIs to:

- Deposit money into an account
- Withdraw money from an account
- Retrieve account balance

## Validation and Exception Handling

The application provides centralized exception handling using Spring's `@RestControllerAdvice`.

Handled scenarios include:

- Resource not found
- Invalid request data
- Duplicate resources
- Generic/unexpected exceptions
- Business validation failures

Error responses follow a consistent structure.

## Database

Microsoft SQL Server is used as the primary relational database.

Spring Data JPA / Hibernate is used for persistence.

## Connection Pooling

HikariCP is used as the application's JDBC connection pool.

Configured pool parameters include:

- Maximum pool size
- Minimum idle connections
- Connection timeout
- Maximum connection lifetime
- Idle timeout

## Caching

Redis is used for application caching.

Redis runs in a Docker container and is accessed by the Spring Boot application through `localhost:6379`.

Spring Cache annotations are used for cache operations.

## Security

Spring Security is configured with HTTP Basic authentication.

The application currently keeps Employee and Account APIs publicly accessible while protecting Actuator endpoints.

## Monitoring

Spring Boot Actuator is enabled for application monitoring.

Exposed endpoints include:

- Health
- Metrics
- Mappings
- Caches
- Info
- Beans
- Environment

Liveness and readiness probes are also enabled.

## API Testing

Postman is used for API testing.

The Postman collection contains:

- Employee API requests
- Account API requests
- Environment variables
- Pre-request scripts
- Response/test scripts
- Response schema validation
- Request chaining using variables

The collection can be executed from the command line using Newman.

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| Spring Boot | Backend framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| Microsoft SQL Server | Relational database |
| HikariCP | JDBC connection pooling |
| Spring Cache | Application caching abstraction |
| Redis | Distributed caching |
| Docker | Redis container |
| Spring Security | API security |
| Spring Boot Actuator | Monitoring and health |
| Postman | API testing |
| Newman | CLI API test execution |
| Maven | Build and dependency management |

---

# Project Structure

```text
EmployeeManagement/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/tns/empmanagement/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── exception/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       └── application.yml
│   │
│   └── test/
│
├── docs/
│   ├── api-documentation.md
│   ├── validation-and-exception-handling.md
│   ├── database-and-hikaricp.md
│   ├── caching-and-redis.md
│   ├── postman-newman-testing.md
│   ├── actuator-monitoring.md
│   └── security.md
│
├── pom.xml
└── README.md
```

---

# API Overview

Base URL:

```text
http://localhost:8080
```

Employee API base path:

```text
/api/v1/employees
```

Account API base path:

```text
/api/v1/accounts
```

---

# Banking APIs

## Deposit Amount

### Endpoint

```http
POST /api/v1/accounts/{id}/deposit
```

### Example

```http
POST http://localhost:8080/api/v1/accounts/1/deposit
```

### Request Body

```json
{
  "amount": 1000
}
```

### Success Response

```text
HTTP 200 OK
```

---

## Withdraw Amount

### Endpoint

```http
POST /api/v1/accounts/{id}/withdraw
```

### Example

```http
POST http://localhost:8080/api/v1/accounts/1/withdraw
```

### Request Body

```json
{
  "amount": 1000
}
```

### Possible Responses

```text
HTTP 200 OK
HTTP 400 Bad Request
```

---

## Get Account Balance

### Endpoint

```http
GET /api/v1/accounts/{id}/balance
```

### Example

```http
GET http://localhost:8080/api/v1/accounts/1/balance
```

### Possible Responses

```text
HTTP 200 OK
HTTP 404 Not Found
```

---

# Employee APIs

## Get All Employees

### Endpoint

```http
GET /api/v1/employees
```

### Example

```http
GET http://localhost:8080/api/v1/employees
```

The endpoint returns employees using pagination.

### Success Response

```text
HTTP 200 OK
```

---

## Get Employee by ID

### Endpoint

```http
GET /api/v1/employees/{id}
```

### Example

```http
GET http://localhost:8080/api/v1/employees/16
```

### Possible Responses

```text
HTTP 200 OK
HTTP 404 Not Found
```

---

## Create Employee

### Endpoint

```http
POST /api/v1/employees
```

### Example Request

```json
{
  "firstName": "Vamsi",
  "lastName": "Yalla",
  "email": "vamsi@example.com",
  "department": "IT",
  "salary": 50000
}
```

### Success Response

```text
HTTP 201 Created
```

---

## Update Employee

### Endpoint

```http
PUT /api/v1/employees/{id}
```

### Example

```http
PUT http://localhost:8080/api/v1/employees/16
```

### Request Body

```json
{
  "firstName": "Vamsi Pavan",
  "lastName": "Yalla",
  "email": "vamsi@example.com",
  "department": "IT",
  "salary": 50000
}
```

### Possible Responses

```text
HTTP 200 OK
HTTP 404 Not Found
```

---

## Delete Employee

### Endpoint

```http
DELETE /api/v1/employees/{id}
```

### Example

```http
DELETE http://localhost:8080/api/v1/employees/16
```

### Possible Responses

```text
HTTP 204 No Content
HTTP 404 Not Found
```

---

# Validation and Error Handling

The application implements centralized exception handling using:

```java
@RestControllerAdvice
```

The global exception handler handles application exceptions and validation failures.

Supported scenarios include:

| Scenario | HTTP Status |
|---|---:|
| Resource not found | 404 |
| Invalid input | 400 |
| Duplicate email/resource | 409 |
| Business rule violation | 422 |
| Unexpected server error | 500 |

The application uses an `ErrorResponse` record containing information such as:

- Timestamp
- HTTP status
- Message
- Validation errors
- Request path

Internal implementation details should not be exposed to API clients.

See:

[Validation and Exception Handling](docs/validation-and-exception-handling.md)

---

# Database

The application uses Microsoft SQL Server.

Example configuration:

```yaml
spring:
  datasource:
    url: jdbc:sqlserver://HOST\SQLEXPRESS;databaseName=EmployeeVPDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

JPA/Hibernate configuration:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

The database schema is managed using Hibernate's configured DDL strategy.

See:

[Database and HikariCP](docs/database-and-hikaricp.md)

---

# HikariCP Connection Pool

HikariCP is used as the application's DataSource connection pool.

Current configuration:

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

### Configuration

| Property | Value | Description |
|---|---:|---|
| `pool-name` | EmployeePool | Name of connection pool |
| `maximum-pool-size` | 10 | Maximum database connections |
| `minimum-idle` | 5 | Minimum idle connections |
| `connection-timeout` | 30000 ms | Maximum wait for connection |
| `max-lifetime` | 1800000 ms | Maximum connection lifetime |
| `idle-timeout` | 600000 ms | Idle connection timeout |

---

# Redis Caching

Redis is used as the caching provider.

Redis runs using Docker.

Application configuration:

```yaml
spring:
  cache:
    type: redis

  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60s
```

The application uses Spring's caching abstraction.

Typical cache annotations include:

```java
@Cacheable
```

for read operations,

```java
@CachePut
```

for updating cached values, and

```java
@CacheEvict
```

for removing cached values.

See:

[Caching and Redis](docs/caching-and-redis.md)

---

# Spring Security

Spring Security is configured with HTTP Basic authentication.

The current security configuration protects Actuator endpoints while allowing the application APIs to remain publicly accessible.

Conceptually:

```text
/actuator/**     -> Basic Authentication required

/api/v1/**      -> Public access
```

Example configuration:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").authenticated()
    .anyRequest().permitAll()
)
.httpBasic(Customizer.withDefaults());
```

CSRF is disabled because this application exposes stateless REST APIs.

See:

[Security](docs/security.md)

---

# Spring Boot Actuator

Spring Boot Actuator is used for application monitoring.

Configured endpoints include:

```text
/actuator/health
/actuator/metrics
/actuator/mappings
/actuator/caches
/actuator/info
/actuator/beans
/actuator/env
```

Health details are enabled:

```yaml
management:
  endpoint:
    health:
      show-details: always
```

Liveness and readiness probes are enabled:

```yaml
management:
  endpoint:
    health:
      probes:
        enabled: true
```

The application also enables liveness and readiness state indicators.

Actuator endpoints are protected using Spring Security.

See:

[Actuator and Monitoring](docs/actuator-monitoring.md)

---

# API Testing with Postman

Postman is used to test the Employee and Account APIs.

The Postman collection is organized by feature:

```text
API Testing
│
├── Employee
│   ├── Get all Employees
│   ├── Get Employee by ID
│   ├── Add Employee
│   ├── Update Employee
│   └── Delete Employee
│
└── Account
    ├── Deposit Amount
    ├── Withdrawl Amount
    └── Get Balance
```

The Postman environment contains variables such as:

```text
baseUrl
employeeId
accountId
employeeBase
accountBase
username
passwd
token
```

The base URL is:

```text
http://localhost:8080
```

The collection contains:

- Pre-request scripts
- Status-code assertions
- JSON response schema validation
- Environment variables
- Request chaining
- Collection export

---

# Running Tests with Newman

Newman is used to execute the Postman collection from the command line.

Installed Newman version:

```text
6.2.2
```

Verify Newman:

```bash
newman --version
```

From the directory containing the exported collection and environment files:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

Example:

```text
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

Newman executes all requests and reports:

- Request execution
- Response status
- Test scripts
- Pre-request scripts
- Assertions
- Failed tests
- Response time
- Data received

Example successful result:

```text
requests                 8
failed                    0
test-scripts             16
prerequest-scripts        8
```

The Newman command is the CLI equivalent of running the Postman collection through the Postman application.

See:

[Postman and Newman Testing](docs/postman-newman-testing.md)

---

# Prerequisites

The following software is required.

## Java

```text
Java 21
```

Verify:

```bash
java -version
```

Expected environment:

```text
java version "21.0.11"
```

## Maven

```text
Apache Maven 3.9.x
```

Verify:

```bash
mvn -version
```

## Docker

Docker is required for running Redis.

Verify:

```bash
docker --version
```

## Node.js

Node.js is required because Newman runs on Node.js.

Verify Node.js using:

```bash
node -v
```

> Note: `node -version` is not the normal Node.js version command. Use `node -v` or `node --version`.

## Newman

Verify:

```bash
newman --version
```

Current environment:

```text
6.2.2
```

---

# Running Redis with Docker

Redis is required by the application for caching.

A Redis container can be started using:

```bash
docker run -d --name employee-redis -p 6379:6379 redis
```

Verify the container:

```bash
docker ps
```

The application connects to:

```text
localhost:6379
```

---

# Running the Application

## 1. Start SQL Server

Make sure the SQL Server instance and required database are available.

The configured database is:

```text
EmployeeVPDB
```

## 2. Start Redis

Start the Redis Docker container:

```bash
docker start employee-redis
```

If the container does not exist:

```bash
docker run -d --name employee-redis -p 6379:6379 redis
```

## 3. Start the Spring Boot application

Using Maven:

```bash
mvn spring-boot:run
```

Or build the application:

```bash
mvn clean package
```

Then run the generated JAR:

```bash
java -jar target/EmployeeManagement-*.jar
```

The application runs on:

```text
http://localhost:8080
```

---

# Testing the Application

After starting the application, the APIs can be tested using:

1. Postman
2. Newman CLI

For example:

```http
GET http://localhost:8080/api/v1/employees
```

The Actuator health endpoint requires authentication:

```http
GET http://localhost:8080/actuator/health
```

Use the configured Spring Security credentials when accessing protected Actuator endpoints.

---

# Documentation

Detailed documentation is available in the `docs` directory.

| Document | Description |
|---|---|
| [API Documentation](docs/api-documentation.md) | Employee and Banking API details |
| [Validation and Exception Handling](docs/validation-and-exception-handling.md) | Validation, exceptions and error responses |
| [Database and HikariCP](docs/database-and-hikaricp.md) | SQL Server and connection pooling |
| [Caching and Redis](docs/caching-and-redis.md) | Redis caching configuration |
| [Postman and Newman](docs/postman-newman-testing.md) | API testing and CLI execution |
| [Actuator and Monitoring](docs/actuator-monitoring.md) | Health, metrics and monitoring |
| [Security](docs/security.md) | Spring Security configuration |

---

# Current Implementation

The following major areas have been implemented:

- Employee REST APIs
- Banking REST APIs
- Pagination for employee retrieval
- Request validation
- Global exception handling
- Standardized error responses
- Duplicate email handling
- SQL Server database integration
- Hibernate/JPA
- HikariCP connection pooling
- Redis caching
- Docker-based Redis
- Spring Security Basic Authentication
- Spring Boot Actuator
- Health monitoring
- Liveness and readiness probes
- Postman API collection
- Postman environment variables
- Pre-request scripts
- Response test scripts
- JSON schema validation
- Newman CLI collection execution

---

# Future Improvements

The following can be considered for further improvement:

- Increase automated Java unit-test coverage
- Add dedicated service-layer unit tests using Mockito
- Add integration tests
- Add custom Actuator health indicators where required
- Add automated load testing for HikariCP tuning
- Add cache hit/miss monitoring dashboards
- Introduce CI/CD pipeline
- Externalize sensitive credentials using environment variables or a secrets manager
- Add Docker Compose for SQL Server and Redis
- Add API documentation using OpenAPI/Swagger
- Add automated Newman execution in CI/CD

---

# Author

Developed as part of a Spring Boot backend/API testing exercise.
