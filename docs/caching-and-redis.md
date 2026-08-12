# Caching and Redis

## 1. Overview

The Employee Management application uses **Spring Cache** with **Redis** to improve application performance by caching frequently accessed data.

Redis runs in a **Docker container** and is used as the caching data store.

The application uses:

- Spring Boot
- Spring Cache
- Redis
- Docker
- Spring Data Redis
- `@Cacheable`
- `@CachePut`
- `@CacheEvict`

The general architecture is:

    Client
      |
      v
    Controller
      |
      v
    Service
      |
      +----------------------+
      |                      |
      v                      v
    Redis Cache          SQL Server
      |                      |
      |                      v
      |                 Persistent Data
      |
      v
    Cached Response

Redis is used to reduce unnecessary database access for cacheable operations.

---

## 2. Why Caching Is Used

Caching is used to improve application performance by storing frequently accessed data in memory.

Without caching, a read operation typically follows:

    Client
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
    SQL Server
      |
      v
    Response

With caching:

    Client
      |
      v
    Controller
      |
      v
    Service
      |
      v
    Redis Cache
      |
      +---- Cache Hit ----> Return Cached Data
      |
      +---- Cache Miss ---> SQL Server
                                |
                                v
                           Store in Redis
                                |
                                v
                             Response

The main benefit is that repeated read operations can be served from Redis instead of querying SQL Server every time.

---

## 3. Redis

Redis is an in-memory data store that is used by the application as a cache.

Redis provides very fast read and write operations and is commonly used for:

- Application caching
- Session storage
- Frequently accessed data
- Distributed caching
- Temporary application data

In this application, Redis is used specifically as a caching layer.

---

## 4. Redis with Docker

Redis is running in a Docker container.

This avoids the need to install Redis directly on the development machine.

The architecture is:

    Spring Boot Application
             |
             | localhost:6379
             v
        Docker Container
             |
             v
           Redis

The application connects to Redis through:

    host: localhost
    port: 6379

---

## 5. Redis Connection Configuration

The application is configured to connect to Redis using the following settings:

    spring:
      data:
        redis:
          host: localhost
          port: 6379
          timeout: 60s

The important properties are:

| Property | Value | Description |
|---|---|---|
| `host` | `localhost` | Redis host |
| `port` | `6379` | Default Redis port |
| `timeout` | `60s` | Redis operation timeout |

---

## 6. Redis Docker Container

The Redis instance is running inside Docker.

The application communicates with the Redis container through the exposed Redis port:

    6379

The communication flow is:

    Spring Boot
        |
        | localhost:6379
        v
    Docker
        |
        v
    Redis Container
        |
        v
    Redis Cache

The Redis container must be running before the application attempts to use Redis.

---

## 7. Checking Docker

Docker can be verified from the command line using:

    docker --version

The development environment uses Docker to run Redis.

The Redis container can be checked using:

    docker ps

This displays currently running containers.

---

## 8. Redis Container Management

Typical Docker commands for Redis include:

    docker ps

To view Redis container logs:

    docker logs <redis-container-name>

To stop a Redis container:

    docker stop <redis-container-name>

To start an existing Redis container:

    docker start <redis-container-name>

The exact container name depends on the Redis Docker container configuration.

---

## 9. Spring Cache

The application uses Spring's caching abstraction.

Spring Cache provides annotations that allow application methods to interact with a cache without requiring cache-specific logic inside every method.

The main annotations used for the caching strategy are:

- `@Cacheable`
- `@CachePut`
- `@CacheEvict`

The caching abstraction can work with Redis as the underlying cache provider.

---

## 10. Enabling Caching

Caching is enabled in the Spring Boot application using:

    @EnableCaching

This enables Spring's annotation-driven cache management.

Once caching is enabled, methods annotated with cache annotations can participate in the caching mechanism.

The general flow is:

    Application Starts
          |
          v
    @EnableCaching
          |
          v
    Spring Cache Enabled
          |
          v
    @Cacheable / @CachePut / @CacheEvict
          |
          v
    Redis

---

## 11. @Cacheable

`@Cacheable` is used for read operations.

When a method annotated with `@Cacheable` is called, Spring first checks whether the requested data is already available in the cache.

If the value exists:

    Request
      |
      v
    @Cacheable
      |
      v
    Redis
      |
      v
    Cache Hit
      |
      v
    Return Cached Data

If the value does not exist:

    Request
      |
      v
    @Cacheable
      |
      v
    Redis
      |
      v
    Cache Miss
      |
      v
    Execute Method
      |
      v
    SQL Server
      |
      v
    Store Result in Redis
      |
      v
    Return Result

This reduces repeated database queries.

---

## 12. Employee Read Operations

Employee read operations are good candidates for caching because employee information may be requested repeatedly.

For example:

    GET /api/v1/employees/{id}

A cacheable service operation can follow this pattern:

    Request Employee
          |
          v
    Check Redis
          |
       +--+--+
       |     |
      Hit   Miss
       |     |
       v     v
    Return  Query DB
    Cache     |
              v
          Store Cache
              |
              v
           Response

The exact cache annotation and cache name should match the implementation used by the service layer.

---

## 13. @CachePut

`@CachePut` is used when the underlying method should execute and the returned result should also update the cache.

This is useful for update operations.

For example:

    PUT /api/v1/employees/{id}

The general flow is:

    Update Employee
          |
          v
    SQL Server
          |
          v
    Updated Employee
          |
          v
    Update Redis Cache
          |
          v
    Return Response

Unlike `@Cacheable`, `@CachePut` does not skip the method execution simply because a cache entry already exists.

The method executes and the returned result is used to update the corresponding cache entry.

---

## 14. @CacheEvict

`@CacheEvict` is used to remove data from the cache.

This is especially useful when a resource is deleted.

For example:

    DELETE /api/v1/employees/{id}

The general flow is:

    Delete Employee
          |
          v
    SQL Server
          |
          v
    Delete Successful
          |
          v
    Remove Employee from Redis
          |
          v
    Return Response

Removing stale data prevents deleted resources from remaining available through the cache.

---

## 15. Cache Consistency

Cache consistency is important when data is changed.

If data is updated in SQL Server but an old value remains in Redis, the application could return stale data.

Therefore, cache operations should be coordinated with database operations.

Typical strategies are:

### Read

Use:

    @Cacheable

### Update

Use:

    @CachePut

### Delete

Use:

    @CacheEvict

The general strategy is:

    CREATE
       |
       v
    Database
       |
       v
    Cache as required

    READ
       |
       v
    Cache
       |
       +---- Miss ---> Database ---> Cache

    UPDATE
       |
       v
    Database
       |
       v
    Update Cache

    DELETE
       |
       v
    Database
       |
       v
    Evict Cache

---

## 16. Cache Key Strategy

A cache key uniquely identifies an item stored in the cache.

For employee data, an employee ID is a natural key.

For example:

    employee:1
    employee:2
    employee:3

For account data:

    account:1
    account:2
    account:3

A consistent key strategy helps prevent collisions between different types of cached data.

The cache key should uniquely identify the corresponding application resource.

---

## 17. Cache Names

Cache names should be organized according to the type of data being cached.

Possible logical cache names include:

    employees
    employee
    accounts
    account

The exact cache names depend on the annotations and cache configuration used in the application.

Using meaningful cache names makes cache monitoring and maintenance easier.

---

## 18. Cache TTL

TTL means **Time To Live**.

TTL determines how long a cache entry should remain available before it expires.

For example:

    Cache Entry Created
            |
            v
          TTL
            |
            v
       Entry Expires
            |
            v
       Next Request
            |
            v
       Cache Miss
            |
            v
       Load From DB

TTL is useful because cached data should not remain indefinitely when the underlying database data may change.

The cache TTL should be selected based on how frequently the underlying data changes and how stale the application can tolerate the cached value becoming.

---

## 19. Cache Maximum Size

A cache can also have a maximum number of entries or memory limit.

Limiting cache size helps prevent uncontrolled memory usage.

A suitable cache size depends on:

- Number of records
- Object size
- Available memory
- Request frequency
- Cache hit ratio
- Application workload

Redis itself provides mechanisms for managing memory usage and eviction.

---

## 20. Cache Eviction

When Redis reaches its configured memory limit, Redis can remove entries according to its configured eviction policy.

Application-level eviction can also happen when:

- An entity is deleted
- An entity is updated
- A cache entry expires
- A cache is explicitly cleared

The application should ensure that changes to persistent data do not leave incorrect cached values.

---

## 21. Cache Hit and Cache Miss

A **cache hit** occurs when requested data is found in Redis.

Example:

    Request
      |
      v
    Redis
      |
      v
    Data Found
      |
      v
    Cache Hit
      |
      v
    Return Data

A **cache miss** occurs when requested data is not available.

Example:

    Request
      |
      v
    Redis
      |
      v
    Data Not Found
      |
      v
    Cache Miss
      |
      v
    Query SQL Server
      |
      v
    Store Result in Redis

A high cache hit ratio generally indicates that caching is effectively reducing database access.

---

## 22. Cache Monitoring

The application exposes Spring Boot Actuator endpoints for monitoring.

The configured Actuator endpoints include:

    health
    metrics
    mappings
    caches
    info
    beans
    env

The `caches` endpoint is particularly useful for inspecting cache information.

The `metrics` endpoint can also be used to inspect application metrics.

Example:

    GET /actuator/caches

Example:

    GET /actuator/metrics

The exact metrics available depend on the application's dependencies and configuration.

---

## 23. Cache Metrics

The application enables cache-related metrics using:

    management:
      metrics:
        enable:
          cache: true

Cache metrics can help monitor:

- Cache hits
- Cache misses
- Cache usage
- Cache performance
- Cache effectiveness

Monitoring these metrics helps determine whether caching is actually improving application performance.

---

## 24. Cache Hit Ratio

The cache hit ratio is an important metric.

It can be represented as:

    Cache Hit Ratio =
        Cache Hits / (Cache Hits + Cache Misses)

For example:

    Cache Hits   = 90
    Cache Misses = 10

Then:

    Cache Hit Ratio = 90 / (90 + 10)
                    = 90%

A high hit ratio generally means that many requests are being served from the cache.

A low hit ratio may indicate:

- Low request repetition
- Short TTL
- Poor cache key strategy
- Frequent data changes
- Insufficient cache size
- Cache eviction

---

## 25. Redis and SQL Server Responsibilities

The application uses Redis and SQL Server for different purposes.

### SQL Server

SQL Server is the primary persistent data store.

It stores the actual application data.

### Redis

Redis is the caching layer.

It stores temporary copies of frequently accessed data to improve read performance.

The relationship is:

    SQL Server
    Persistent Source of Truth
            |
            v
        Application
            |
            v
        Redis Cache

Redis should not be treated as the primary persistent database for the application's employee and account data.

---

## 26. Cache-Aside Pattern

The application's caching strategy can be understood using the cache-aside pattern.

The general process is:

    Application
        |
        v
    Check Redis
        |
        +---- Hit ----> Return Cached Data
        |
        +---- Miss
              |
              v
          Query SQL Server
              |
              v
          Store in Redis
              |
              v
          Return Data

This approach allows the application to control which data is cached.

---

## 27. Read Performance

Caching can improve read performance because Redis is an in-memory data store.

Without caching:

    Request
      |
      v
    SQL Server
      |
      v
    Query Execution
      |
      v
    Response

With caching:

    Request
      |
      v
    Redis
      |
      v
    Cached Response

When the requested data is available in Redis, the database query can be avoided.

This can reduce:

- Database load
- Query execution overhead
- Response latency
- Number of database connections required for repeated reads

---

## 28. Impact on Database Connection Pool

Caching can also reduce pressure on the HikariCP connection pool.

Without caching:

    Many Read Requests
          |
          v
      SQL Server
          |
          v
      HikariCP
          |
          v
    More DB Connections

With effective caching:

    Many Read Requests
          |
          v
       Redis
          |
          +---- Cache Hit
          |
          v
    Fewer DB Queries
          |
          v
    Lower DB Connection Usage

This allows the database connection pool to be used more efficiently.

---

## 29. Redis Availability

Because Redis is an external service running in Docker, the application depends on Redis being available for cache operations.

The Redis service should therefore be monitored during development and deployment.

If Redis becomes unavailable, the application may experience cache-related errors depending on the configured cache behavior.

Redis availability should be considered separately from SQL Server availability.

---

## 30. Development Setup

The development environment consists of:

    Windows
       |
       +----------------------+
       |                      |
       v                      v
    Spring Boot           Docker
       |                      |
       |                      v
       |                 Redis Container
       |                      |
       |                 Port 6379
       |
       v
    SQL Server
       |
       v
    EmployeeVPDB

The Spring Boot application communicates with:

    SQL Server -> Persistent Data

and:

    Redis -> Cached Data

---

## 31. Starting the Application with Redis

Before testing cache-enabled APIs, ensure that the Redis Docker container is running.

Verify Docker:

    docker --version

Verify running containers:

    docker ps

Verify that the Redis container is running and that port `6379` is available.

Then start the Spring Boot application.

The application uses:

    localhost:6379

to connect to Redis.

---

## 32. Testing Cached APIs

Cached APIs can be tested using Postman.

A typical test sequence is:

### First Request

    GET /api/v1/employees/1

The first request may result in a cache miss.

The application retrieves the employee from SQL Server and stores the result in Redis.

### Second Request

    GET /api/v1/employees/1

The second request can be served from Redis if the cache entry is still available.

The sequence is:

    First Request
         |
         v
    Redis Cache Miss
         |
         v
    SQL Server
         |
         v
    Store in Redis
         |
         v
    Response

    Second Request
         |
         v
    Redis Cache Hit
         |
         v
    Response

---

## 33. Testing Cache Updates

When an employee is updated, the cache should contain the updated information.

Example:

    PUT /api/v1/employees/1

The expected flow is:

    Update Request
          |
          v
      SQL Server
          |
          v
      Updated Data
          |
          v
      Update Cache
          |
          v
       Response

A subsequent GET request should return the updated employee data rather than an outdated cached value.

---

## 34. Testing Cache Eviction

When an employee is deleted:

    DELETE /api/v1/employees/1

The employee should also be removed from the relevant cache.

The expected flow is:

    Delete Request
          |
          v
      SQL Server
          |
          v
       Delete Data
          |
          v
      Evict Cache
          |
          v
       Response

A subsequent request for the deleted employee should not return stale cached data.

---

## 35. Cache Testing Strategy

Cache testing should verify:

- First request behavior
- Cache miss behavior
- Cache hit behavior
- Cache key correctness
- Cache update behavior
- Cache eviction behavior
- TTL expiration
- Redis availability
- Cache metrics
- Data consistency between Redis and SQL Server

---

## 36. Postman and Newman Testing

The application APIs are tested using Postman.

The Postman collection contains:

- Employee API requests
- Account API requests
- Environment variables
- Pre-request scripts
- Test scripts
- Response schema validation

The collection can also be executed using Newman.

Example:

    newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"

Newman can be used to repeatedly execute API tests after application changes.

Caching-related API behavior can therefore be included in automated API testing.

---

## 37. Cache Monitoring with Actuator

Spring Boot Actuator provides endpoints that can help monitor cache behavior.

Configured endpoints include:

    /actuator/health
    /actuator/metrics
    /actuator/mappings
    /actuator/caches
    /actuator/info
    /actuator/beans
    /actuator/env

The cache endpoint provides information about configured caches.

The metrics endpoint provides application metrics and cache-related metrics when supported by the configured cache implementation.

---

## 38. Health and Redis

The application also exposes Spring Boot Actuator health information.

The health endpoint is:

    GET /actuator/health

Redis health information may be included when the Redis health indicator is available and enabled by the application's configuration.

The health endpoint can therefore help identify whether configured external dependencies are available.

---

## 39. Security Considerations

Redis should not be unnecessarily exposed directly to the public network.

For development, the application uses:

    localhost:6379

For production deployments, Redis should be protected using appropriate network controls and authentication/security configuration.

Sensitive Redis configuration should not be committed directly to source control.

Similarly, Actuator endpoints should be secured appropriately because monitoring endpoints can expose application information.

---

## 40. Common Redis Problems

### Redis Container Not Running

If the Redis Docker container is stopped, the application may not be able to perform cache operations.

Check:

    docker ps

### Wrong Redis Port

The application expects:

    6379

Verify that the Redis container exposes the correct port.

### Wrong Host

The application is configured to use:

    localhost

If Redis is running in another environment or container network, the host configuration may need to be changed.

### Stale Cache

If cached data is not updated or evicted correctly, the application may return outdated information.

Review:

- Cache keys
- `@CachePut`
- `@CacheEvict`
- TTL
- Cache configuration

---

## 41. Cache Failure Investigation

When cache behavior is not working as expected, investigate in the following order:

    1. Check Docker
           |
           v
    2. Check Redis container
           |
           v
    3. Check Redis port
           |
           v
    4. Check Spring Redis configuration
           |
           v
    5. Check cache annotations
           |
           v
    6. Check cache names
           |
           v
    7. Check cache keys
           |
           v
    8. Check Actuator cache information
           |
           v
    9. Check application logs
           |
           v
    10. Check Redis data

---

## 42. Benefits of Redis Caching

Using Redis provides several potential benefits:

- Fast in-memory access
- Reduced database load
- Reduced repeated SQL queries
- Improved response time for repeated reads
- Distributed cache capability
- Centralized cache storage
- Better scalability for multiple application instances

---

## 43. Limitations of Caching

Caching also introduces additional considerations.

Potential issues include:

- Stale data
- Cache invalidation complexity
- Additional infrastructure
- Redis availability requirements
- Memory usage
- Cache key management
- TTL configuration
- Data serialization and deserialization
- Cache consistency

Caching should therefore be applied selectively to operations where it provides a measurable benefit.

---

## 44. Recommended Caching Practices

Recommended practices include:

- Cache frequently accessed data
- Use meaningful cache names
- Use predictable cache keys
- Define appropriate TTL values
- Evict stale data
- Update cache after successful updates
- Monitor cache hit/miss ratios
- Monitor Redis memory usage
- Avoid caching unnecessarily large objects
- Avoid caching sensitive information without proper protection
- Test cache behavior separately from database behavior

---

## 45. Production Considerations

For production environments, the following should be considered:

- Redis authentication
- Redis network security
- Redis persistence requirements
- Redis memory limits
- Eviction policies
- High availability
- Redis monitoring
- Connection timeout configuration
- Cache TTL
- Cache invalidation
- Application instance scaling

If multiple Spring Boot instances are deployed, Redis can provide a shared cache rather than maintaining a separate local cache for each application instance.

---

## 46. Distributed Caching

One benefit of Redis is that it can act as a shared cache for multiple application instances.

For example:

    Application Instance 1
             |
             |
             v
           Redis
             ^
             |
             |
    Application Instance 2

Both application instances can access the same cached data.

This is useful when scaling the application horizontally.

---

## 47. Cache Architecture

The application's caching architecture can be summarized as:

    +----------------------+
    |       Client         |
    +----------+-----------+
               |
               v
    +----------------------+
    |   Spring Boot API    |
    +----------+-----------+
               |
               v
    +----------------------+
    |     Service Layer    |
    +----------+-----------+
               |
          Cache Check
               |
         +-----+-----+
         |           |
       Hit          Miss
         |           |
         v           v
      Redis      SQL Server
         |           |
         |           v
         |       Persistent
         |          Data
         |           |
         |           v
         |         Redis
         |           |
         +-----+-----+
               |
               v
            Response

---

## 48. Relationship with HikariCP

Redis caching and HikariCP work together to improve overall application efficiency.

HikariCP manages SQL Server connections.

Redis reduces the number of requests that need to reach SQL Server.

The relationship is:

    API Request
         |
         v
       Redis
         |
         +---- Hit ----> Response
         |
         +---- Miss
                |
                v
            HikariCP
                |
                v
            SQL Server
                |
                v
            Store in Redis
                |
                v
             Response

Effective caching can therefore reduce pressure on the SQL Server connection pool.

---

## 49. Current Configuration Summary

The application's caching configuration can be summarized as follows:

| Component | Configuration |
|---|---|
| Cache Technology | Spring Cache |
| Cache Provider | Redis |
| Redis Deployment | Docker Container |
| Redis Host | `localhost` |
| Redis Port | `6379` |
| Redis Timeout | `60s` |
| Cache Metrics | Enabled |
| Cache Monitoring | Spring Boot Actuator |
| Cache Endpoint | `/actuator/caches` |
| Metrics Endpoint | `/actuator/metrics` |
| Persistent Database | Microsoft SQL Server |

---

## 50. Summary

The Employee Management application uses Redis as a caching layer in front of the Microsoft SQL Server database.

Redis runs inside a Docker container and is accessed by the Spring Boot application through:

    localhost:6379

Spring Cache provides the abstraction for cache operations, while Redis provides the actual cache storage.

The caching strategy includes:

- `@EnableCaching`
- `@Cacheable` for read operations
- `@CachePut` for updates
- `@CacheEvict` for deletions
- Cache key strategies
- TTL considerations
- Cache monitoring
- Cache hit/miss monitoring
- Redis running through Docker
- Actuator-based cache monitoring

The overall goal is to reduce unnecessary database access, improve response times for frequently accessed data, and reduce pressure on the SQL Server and HikariCP connection pool.

SQL Server remains the primary persistent data store, while Redis is used as a fast caching layer.