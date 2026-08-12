# Database and HikariCP

## 1. Overview

The Employee Management application uses **Microsoft SQL Server** as its relational database.

Spring Boot uses **HikariCP** as the connection pool for managing database connections.

The database and connection-pool configuration is defined in `application.yml`.

The main components are:

- Microsoft SQL Server
- Spring Data JPA
- Hibernate
- HikariCP
- SQL Server JDBC Driver

The overall architecture is:

    Spring Boot Application
            |
            v
        Spring Data JPA
            |
            v
         Hibernate
            |
            v
          HikariCP
       Connection Pool
            |
            v
      SQL Server Database

---

## 2. Database Technology

The application uses:

**Database:** Microsoft SQL Server

The application connects to the SQL Server database using the Microsoft SQL Server JDBC driver.

The configured database is:

    EmployeeVPDB

The application uses SQL Server Express in the development environment.

---

## 3. Database Connection

The application uses the following JDBC configuration:

    jdbc:sqlserver://TNS-IT-DESKTOP\SQLEXPRESS;databaseName=EmployeeVPDB;integratedSecurity=true;encrypt=true;trustServerCertificate=true

The important connection properties are:

| Property | Description |
|---|---|
| SQL Server Host | `TNS-IT-DESKTOP` |
| SQL Server Instance | `SQLEXPRESS` |
| Database | `EmployeeVPDB` |
| Authentication | Windows Integrated Security |
| Encryption | Enabled |
| Trust Server Certificate | Enabled for the development environment |
| JDBC Driver | Microsoft SQL Server JDBC Driver |

---

## 4. JDBC Driver

The application uses the Microsoft SQL Server JDBC driver to communicate with SQL Server.

The driver class is:

    com.microsoft.sqlserver.jdbc.SQLServerDriver

This allows the Java application to establish JDBC connections to the SQL Server database.

---

## 5. Spring Data JPA

The application uses Spring Data JPA for database access.

JPA provides an abstraction over the underlying database operations.

The general flow is:

    Controller
        |
        v
    Service Layer
        |
        v
    Repository
        |
        v
    Spring Data JPA
        |
        v
    Hibernate
        |
        v
    HikariCP
        |
        v
    SQL Server

This reduces the need to write low-level JDBC connection-management code.

---

## 6. Hibernate

Hibernate is used as the JPA implementation.

Hibernate is responsible for:

- Mapping Java entities to database tables
- Generating SQL queries
- Managing entity persistence
- Managing relationships between entities
- Communicating with the database through JDBC

The application enables SQL output during development.

Configured property:

    spring:
      jpa:
        show-sql: true

SQL formatting is also enabled:

    spring:
      jpa:
        properties:
          hibernate:
            format_sql: true

This makes generated SQL easier to read during development and debugging.

---

## 7. Hibernate DDL Configuration

The application uses:

    spring:
      jpa:
        hibernate:
          ddl-auto: update

The `update` setting allows Hibernate to update the database schema based on entity changes without recreating the entire database.

This is useful during development because existing data can generally be retained while the schema is updated.

For production environments, database schema management should normally be handled through a controlled migration strategy such as Flyway or Liquibase rather than relying on automatic schema updates.

---

## 8. HikariCP

**HikariCP** is the JDBC connection pool used by Spring Boot.

Instead of creating a new database connection for every request, HikariCP maintains a pool of reusable database connections.

Without connection pooling:

    API Request
        |
        v
    Create DB Connection
        |
        v
    Execute Query
        |
        v
    Close DB Connection

With HikariCP:

    API Request
        |
        v
    Get Connection from Pool
        |
        v
    Execute Query
        |
        v
    Return Connection to Pool

Connection pooling improves application performance by reducing the overhead of repeatedly creating and closing database connections.

---

## 9. HikariCP Configuration

The application configures HikariCP using the following settings:

    spring:
      datasource:
        hikari:
          pool-name: EmployeePool
          maximum-pool-size: 10
          minimum-idle: 5
          connection-timeout: 30000
          max-lifetime: 1800000
          idle-timeout: 600000

The configured pool name is:

    EmployeePool

---

## 10. Pool Name

The configured pool name is:

    EmployeePool

A meaningful pool name makes it easier to identify the application's connection pool when monitoring logs and metrics.

---

## 11. Maximum Pool Size

The application uses:

    maximum-pool-size: 10

This means the HikariCP pool can maintain up to **10 active database connections**.

The maximum pool size should be selected according to:

- Application workload
- Number of concurrent requests
- Database capacity
- Available CPU
- Query execution time
- Number of application instances
- Database connection limits

A larger pool is not always better.

Creating too many connections can increase database contention and resource usage.

---

## 12. Minimum Idle Connections

The application uses:

    minimum-idle: 5

This keeps a baseline of approximately five idle connections available for incoming requests when possible.

Maintaining idle connections can reduce the latency associated with creating new database connections when traffic increases.

The value should be chosen according to the application's normal workload.

---

## 13. Connection Timeout

The application uses:

    connection-timeout: 30000

The value is specified in milliseconds.

Therefore:

    30000 ms = 30 seconds

This means a request waiting for a connection from the HikariCP pool can wait up to approximately 30 seconds before the connection acquisition attempt fails.

A connection timeout helps prevent requests from waiting indefinitely when the pool is exhausted.

---

## 14. Maximum Lifetime

The application uses:

    max-lifetime: 1800000

The value is specified in milliseconds.

Therefore:

    1800000 ms = 30 minutes

This controls the maximum lifetime of a connection in the pool.

Connections are periodically retired and replaced rather than being kept indefinitely.

This is useful because database infrastructure or network components may have their own connection lifetime limits.

---

## 15. Idle Timeout

The application uses:

    idle-timeout: 600000

The value is specified in milliseconds.

Therefore:

    600000 ms = 10 minutes

This controls how long an idle connection can remain in the pool before it can be removed, subject to the pool's minimum-idle configuration.

This helps prevent unnecessary long-lived idle connections during periods of low traffic.

---

## 16. HikariCP Configuration Summary

The current configuration is:

| Property | Value | Description |
|---|---:|---|
| `pool-name` | `EmployeePool` | Name of the connection pool |
| `maximum-pool-size` | `10` | Maximum number of connections |
| `minimum-idle` | `5` | Baseline number of idle connections |
| `connection-timeout` | `30000 ms` | Maximum wait for a connection |
| `max-lifetime` | `1800000 ms` | Maximum connection lifetime |
| `idle-timeout` | `600000 ms` | Idle connection timeout |

---

## 17. Connection Pool Lifecycle

A typical database request follows this flow:

    API Request
         |
         v
    Controller
         |
         v
    Service
         |
         v
    Repository
         |
         v
    Hibernate
         |
         v
    HikariCP
         |
         v
    Borrow Connection
         |
         v
    Execute SQL
         |
         v
    Return Connection
         |
         v
    HikariCP Pool

The connection is returned to the pool after the database operation is completed.

It is not normally destroyed after every request.

---

## 18. Connection Pool Exhaustion

Connection pool exhaustion occurs when all available connections are being used and another request needs a connection.

With the current configuration:

    maximum-pool-size = 10

up to ten connections can be active at the same time.

If all connections are busy, additional requests must wait for a connection to become available.

The configured timeout is:

    connection-timeout = 30000 ms

If a connection does not become available within the configured timeout, the request can fail.

---

## 19. Causes of Pool Exhaustion

Connection pool exhaustion can occur because of:

- Too many concurrent requests
- Long-running database queries
- Slow database operations
- Unclosed transactions
- Incorrect connection handling
- Database performance problems
- Insufficient pool size
- Connection leaks
- Large workloads

Increasing the pool size should not automatically be the first solution.

The underlying cause should be investigated first.

---

## 20. Handling Pool Exhaustion

The application should handle database connection failures gracefully.

Recommended practices include:

- Configure a reasonable connection timeout
- Monitor HikariCP metrics
- Investigate slow SQL queries
- Avoid unnecessarily long transactions
- Ensure resources are released correctly
- Monitor database performance
- Tune pool size based on workload
- Avoid creating excessive database connections

The configured 30-second connection timeout prevents requests from waiting indefinitely.

---

## 21. Connection Pool Tuning

The current pool configuration uses:

    maximum-pool-size: 10
    minimum-idle: 5

These values provide a starting point for the application's development workload.

For production workloads, the values should be tuned based on:

- Load testing
- Concurrent request volume
- Database server capacity
- Query execution time
- CPU utilization
- Memory utilization
- Connection utilization
- Application instance count

Pool tuning should be based on measured workload rather than arbitrary values.

---

## 22. Load Testing and Pool Tuning

A typical tuning process is:

    Start with baseline configuration
                |
                v
          Run load test
                |
                v
       Monitor application
                |
                v
       Monitor HikariCP
                |
                v
       Monitor SQL Server
                |
                v
        Identify bottlenecks
                |
                v
        Adjust configuration
                |
                v
          Run test again

Important metrics to observe include:

- Active connections
- Idle connections
- Pending connection requests
- Connection acquisition time
- Connection creation time
- Query execution time
- Request latency
- Error rate

---

## 23. Monitoring HikariCP

HikariCP metrics can be monitored through Spring Boot Actuator when datasource metrics are available.

The application also exposes Actuator metrics.

Configured Actuator endpoints include:

    health
    metrics
    mappings
    caches
    info
    beans
    env

The metrics endpoint can be used to inspect application and datasource-related metrics.

Example:

    GET /actuator/metrics

Specific metrics can be inspected through the metrics endpoint when available.

---

## 24. HikariCP Metrics

Typical datasource pool metrics include information such as:

- Active connections
- Idle connections
- Maximum connections
- Minimum connections
- Pending connection requests

These metrics help determine whether the configured pool size is appropriate.

For example:

    Active connections: 3
    Idle connections: 7
    Maximum connections: 10

This indicates that the pool is not currently under heavy pressure.

A pool consistently operating close to its maximum may require investigation.

---

## 25. Database Monitoring

Database performance should be monitored together with application performance.

Important areas include:

- CPU utilization
- Memory utilization
- Active database connections
- Query execution time
- Blocking queries
- Deadlocks
- Database size
- Transaction duration
- Index performance

Database monitoring is important because increasing the HikariCP pool size does not improve a database that is already overloaded.

---

## 26. SQL Logging

The application currently has SQL logging enabled through:

    spring:
      jpa:
        show-sql: true

Hibernate SQL formatting is also enabled:

    spring:
      jpa:
        properties:
          hibernate:
            format_sql: true

This is useful during development for understanding generated SQL queries.

For production environments, SQL logging should be reviewed carefully because excessive SQL logging can:

- Increase log volume
- Affect performance
- Make logs harder to analyze
- Potentially expose sensitive information

---

## 27. Database Schema Management

The application currently uses:

    ddl-auto: update

This is appropriate for the current development-oriented project because it allows Hibernate to update the schema based on entity definitions.

For production systems, schema changes should preferably be managed using version-controlled database migration tools.

Examples include:

- Flyway
- Liquibase

A migration-based approach provides better control over database schema changes across environments.

---

## 28. Database Security

The application uses Windows Integrated Security for the SQL Server connection.

The JDBC connection contains:

    integratedSecurity=true

The connection also uses:

    encrypt=true

and:

    trustServerCertificate=true

The current configuration is suitable for the local development environment.

For production environments, database security should be reviewed carefully, including:

- Authentication method
- Encryption certificates
- Database credentials
- Network access
- Firewall rules
- Least-privilege database accounts
- Secret management

Sensitive credentials and connection information should not be committed directly to source control.

---

## 29. Development Environment

The database configuration used during development connects to a local SQL Server Express instance.

The database is:

    EmployeeVPDB

The SQL Server instance is:

    SQLEXPRESS

The application runs locally using:

    http://localhost:8080

The database is accessed by the Spring Boot application through the configured JDBC connection.

---

## 30. Database and Application Relationship

The Employee Management application stores persistent application data in SQL Server.

The main data access flow is:

    Employee / Account API
            |
            v
        Service Layer
            |
            v
        Repository
            |
            v
       Spring Data JPA
            |
            v
         Hibernate
            |
            v
         HikariCP
            |
            v
       SQL Server
            |
            v
      EmployeeVPDB

Redis is used separately as the application's caching layer and does not replace SQL Server as the primary relational database.

---

## 31. Database and Cache Separation

The application uses two different data stores for different purposes.

### SQL Server

SQL Server is the primary persistent database.

It is responsible for storing application data that must be persisted.

### Redis

Redis is used for caching frequently accessed data.

The general architecture is:

    Application
        |
        +----------------------+
        |                      |
        v                      v
    SQL Server              Redis
    Persistent Data         Cache
        |                      |
        +----------+-----------+
                   |
                   v
             Application

Redis improves read performance by reducing unnecessary database access for cacheable operations.

---

## 32. Benefits of HikariCP

Using HikariCP provides several benefits:

- Efficient database connection management
- Connection reuse
- Reduced connection creation overhead
- Better application performance
- Configurable connection limits
- Connection timeout support
- Connection lifetime management
- Idle connection management
- Pool monitoring

---

## 33. Recommended Production Improvements

For a production deployment, the following improvements can be considered:

- Use database migration tooling such as Flyway or Liquibase
- Disable unnecessary SQL logging
- Review `ddl-auto` configuration
- Use production-grade database authentication
- Use secure certificate validation
- Store secrets outside source control
- Monitor HikariCP metrics
- Monitor SQL Server performance
- Perform load testing
- Tune pool size based on measurements
- Configure appropriate transaction boundaries
- Investigate slow queries
- Configure database indexes appropriately
- Monitor connection leaks and long-running transactions

---

## 34. Current Configuration Summary

The application's database configuration can be summarized as follows:

| Component | Configuration |
|---|---|
| Database | Microsoft SQL Server |
| Database Name | `EmployeeVPDB` |
| SQL Server Instance | `SQLEXPRESS` |
| ORM | Hibernate |
| Data Access | Spring Data JPA |
| Connection Pool | HikariCP |
| Pool Name | `EmployeePool` |
| Maximum Pool Size | 10 |
| Minimum Idle | 5 |
| Connection Timeout | 30 seconds |
| Maximum Connection Lifetime | 30 minutes |
| Idle Timeout | 10 minutes |
| Schema Strategy | `update` |
| SQL Logging | Enabled |
| SQL Formatting | Enabled |
| Pool Monitoring | Spring Boot Actuator |

---

## 35. Summary

The Employee Management application uses Microsoft SQL Server as its primary relational database and HikariCP for efficient JDBC connection pooling.

The current configuration provides:

- SQL Server database connectivity
- Spring Data JPA integration
- Hibernate ORM
- HikariCP connection pooling
- Maximum pool size of 10
- Minimum idle connections of 5
- 30-second connection acquisition timeout
- 30-minute maximum connection lifetime
- 10-minute idle timeout
- Actuator-based monitoring
- SQL logging for development
- Database schema updates through Hibernate

The connection pool configuration provides a reasonable baseline for the application's development environment.

For production usage, the pool should be validated through load testing and tuned according to actual application and database workload.