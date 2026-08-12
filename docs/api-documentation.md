# API Documentation

## 1. Overview

The Employee Management application is a Spring Boot REST API application that provides two main functional areas:

- Employee Management
- Banking / Account Management

The application exposes RESTful endpoints under the `/api/v1` base path.

The application is designed with:

- Spring Boot
- Spring Data JPA
- Microsoft SQL Server
- HikariCP connection pooling
- Redis caching
- Spring Security
- Spring Boot Actuator
- Postman API testing
- Newman CLI automation

---

## 2. Base URL

When the application is running locally, the server is available at:

`http://localhost:8080`

The API base path is:

`http://localhost:8080/api/v1`

### Employee API Base URL

`http://localhost:8080/api/v1/employees`

### Account API Base URL

`http://localhost:8080/api/v1/accounts`

---

# 3. Employee API

The Employee API provides CRUD operations for managing employee records.

Available operations:

1. Get all employees
2. Get employee by ID
3. Add employee
4. Update employee
5. Delete employee

---

## 3.1 Get All Employees

Retrieves a paginated list of employees.

### Request

**Method:** `GET`

**Endpoint:**

`/api/v1/employees`

**Full URL:**

`http://localhost:8080/api/v1/employees`

### Example

`GET http://localhost:8080/api/v1/employees`

### Pagination

The endpoint supports pagination using query parameters.

Example:

`GET http://localhost:8080/api/v1/employees?page=0&size=10`

| Parameter | Type | Description |
|---|---|---|
| `page` | Integer | Page number, starting from 0 |
| `size` | Integer | Number of records per page |

### Success Response

**HTTP 200 OK**

Example response:

    {
      "content": [
        {
          "id": 1,
          "firstName": "John",
          "lastName": "Doe",
          "email": "john.doe@example.com",
          "department": "IT",
          "salary": 50000
        }
      ],
      "totalPages": 1,
      "totalElements": 1,
      "size": 10,
      "number": 0,
      "numberOfElements": 1,
      "first": true,
      "last": true,
      "empty": false
    }

### Response Fields

| Field | Type | Description |
|---|---|---|
| `content` | Array | List of employee records |
| `totalPages` | Integer | Total number of pages |
| `totalElements` | Integer | Total number of employees |
| `size` | Integer | Requested page size |
| `number` | Integer | Current page number |
| `numberOfElements` | Integer | Number of employees in the current page |
| `first` | Boolean | Indicates whether this is the first page |
| `last` | Boolean | Indicates whether this is the last page |
| `empty` | Boolean | Indicates whether the result is empty |

---

## 3.2 Get Employee By ID

Retrieves a specific employee using the employee ID.

### Request

**Method:** `GET`

**Endpoint:**

`/api/v1/employees/{id}`

### Example

`GET http://localhost:8080/api/v1/employees/1`

### Path Variable

| Variable | Type | Description |
|---|---|---|
| `id` | Integer | Unique employee identifier |

### Success Response

**HTTP 200 OK**

Example:

    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "department": "IT",
      "salary": 50000
    }

### Employee Not Found

If the requested employee does not exist:

**HTTP 404 Not Found**

The application handles this through the global exception handling mechanism.

---

## 3.3 Add Employee

Creates a new employee.

### Request

**Method:** `POST`

**Endpoint:**

`/api/v1/employees`

**Full URL:**

`http://localhost:8080/api/v1/employees`

### Request Headers

`Content-Type: application/json`

### Request Body

    {
      "firstName": "Vamsi",
      "lastName": "Yalla",
      "email": "vamsi@example.com",
      "department": "IT",
      "salary": 50000
    }

### Success Response

**HTTP 201 Created**

Example:

    {
      "id": 1,
      "firstName": "Vamsi",
      "lastName": "Yalla",
      "email": "vamsi@example.com",
      "department": "IT",
      "salary": 50000
    }

### Possible Error Responses

Invalid input:

**HTTP 400 Bad Request**

Duplicate email:

**HTTP 409 Conflict**

---

## 3.4 Update Employee

Updates an existing employee.

### Request

**Method:** `PUT`

**Endpoint:**

`/api/v1/employees/{id}`

### Example

`PUT http://localhost:8080/api/v1/employees/1`

### Request Headers

`Content-Type: application/json`

### Request Body

    {
      "firstName": "Vamsi Pavan",
      "lastName": "Yalla",
      "email": "vamsipavan@example.com",
      "department": "IT",
      "salary": 55000
    }

### Success Response

**HTTP 200 OK**

Example:

    {
      "id": 1,
      "firstName": "Vamsi Pavan",
      "lastName": "Yalla",
      "email": "vamsipavan@example.com",
      "department": "IT",
      "salary": 55000
    }

### Employee Not Found

**HTTP 404 Not Found**

### Duplicate Email

If the updated email already belongs to another employee:

**HTTP 409 Conflict**

---

## 3.5 Delete Employee

Deletes an employee using the employee ID.

### Request

**Method:** `DELETE`

**Endpoint:**

`/api/v1/employees/{id}`

### Example

`DELETE http://localhost:8080/api/v1/employees/1`

### Success Response

**HTTP 204 No Content**

A successful delete operation does not return a response body.

### Employee Not Found

**HTTP 404 Not Found**

---

# 4. Employee Data Model

The Employee API uses the following fields:

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Unique employee identifier |
| `firstName` | String | Employee first name |
| `lastName` | String | Employee last name |
| `email` | String | Employee email address |
| `department` | String | Employee department |
| `salary` | Number | Employee salary |

---

# 5. Account / Banking API

The Account API provides basic banking operations.

Available operations:

1. Deposit amount
2. Withdraw amount
3. Get account balance

---

## 5.1 Deposit Amount

Deposits money into an account.

### Request

**Method:** `POST`

**Endpoint:**

`/api/v1/accounts/{id}/deposit`

### Example

`POST http://localhost:8080/api/v1/accounts/1/deposit`

### Request Headers

`Content-Type: application/json`

### Request Body

    {
      "amount": 1000
    }

### Success Response

**HTTP 200 OK**

Example:

    {
      "id": 1,
      "accountHolder": "Vamsi",
      "balance": 11000
    }

### Response Fields

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Account identifier |
| `accountHolder` | String | Account holder name |
| `balance` | Number | Current account balance |

---

## 5.2 Withdraw Amount

Withdraws money from an account.

### Request

**Method:** `POST`

**Endpoint:**

`/api/v1/accounts/{id}/withdraw`

### Example

`POST http://localhost:8080/api/v1/accounts/1/withdraw`

### Request Headers

`Content-Type: application/json`

### Request Body

    {
      "amount": 1000
    }

### Success Response

**HTTP 200 OK**

Example:

    {
      "id": 1,
      "accountHolder": "Vamsi",
      "balance": 9000
    }

### Business Rule Violation

If the withdrawal violates a banking business rule, the application can return an appropriate client error response.

Possible responses include:

**HTTP 400 Bad Request**

or:

**HTTP 422 Unprocessable Entity**

The exact response depends on the business validation implemented by the application.

---

## 5.3 Get Account Balance

Retrieves the current balance of an account.

### Request

**Method:** `GET`

**Endpoint:**

`/api/v1/accounts/{id}/balance`

### Example

`GET http://localhost:8080/api/v1/accounts/1/balance`

### Success Response

**HTTP 200 OK**

Example:

    10000.0

The balance endpoint returns the current account balance as a numeric value.

### Account Not Found

**HTTP 404 Not Found**

---

# 6. Account Data Model

The Account API uses the following fields:

| Field | Type | Description |
|---|---|---|
| `id` | Integer | Unique account identifier |
| `accountHolder` | String | Account holder name |
| `balance` | Number | Current account balance |

---

# 7. HTTP Status Codes

The application follows standard HTTP status codes.

| Status Code | Meaning | Usage |
|---|---|---|
| `200 OK` | Request completed successfully | GET, PUT, deposit, withdrawal |
| `201 Created` | Resource successfully created | Employee creation |
| `204 No Content` | Request successful without response body | Employee deletion |
| `400 Bad Request` | Invalid request or input | Validation failures |
| `404 Not Found` | Requested resource does not exist | Employee or account not found |
| `409 Conflict` | Resource conflict | Duplicate employee email |
| `422 Unprocessable Entity` | Business rule violation | Business validation failures |
| `500 Internal Server Error` | Unexpected server error | Unhandled application errors |

---

# 8. Validation

The application validates incoming request data before processing it.

Examples of invalid input include:

- Missing required fields
- Invalid email address
- Invalid salary
- Invalid amount
- Other validation constraints defined by the application

Invalid input results in:

**HTTP 400 Bad Request**

Validation failures are handled centrally using the global exception handling mechanism.

---

# 9. Error Response Format

The application uses a centralized error response structure.

The error response contains:

- `timestamp`
- `status`
- `message`
- `errors`
- `path`

### Example

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 404,
      "message": "Employee not found",
      "errors": [],
      "path": "/api/v1/employees/999"
    }

### Validation Error Example

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 400,
      "message": "Validation failed",
      "errors": [
        "firstName must not be blank",
        "email must be a valid email address"
      ],
      "path": "/api/v1/employees"
    }

The application is designed to return user-friendly error messages without exposing internal implementation details.

---

# 10. API Structure

The complete API structure is:

    /api/v1
    |
    +-- /employees
    |   |
    |   +-- GET    /
    |   +-- GET    /{id}
    |   +-- POST   /
    |   +-- PUT    /{id}
    |   +-- DELETE /{id}
    |
    +-- /accounts
        |
        +-- POST /{id}/deposit
        +-- POST /{id}/withdraw
        +-- GET  /{id}/balance

---

# 11. Postman API Testing

The APIs are tested using Postman.

The Postman collection is organized according to API modules.

    API Testing
    |
    +-- Employee
    |   |
    |   +-- Get all Employees
    |   +-- Get Employee by ID
    |   +-- Add Employee
    |   +-- Update Employee
    |   +-- Delete Employee
    |
    +-- Account
        |
        +-- Deposit Amount
        +-- Withdrawl Amount
        +-- Get Balance

The Postman environment contains variables including:

- `baseUrl`
- `employeeId`
- `accountId`
- `token`
- `employeeBase`
- `accountBase`
- `username`
- `passwd`

Example values:

`baseUrl = http://localhost:8080`

`employeeBase = api/v1/employees`

`accountBase = api/v1/accounts`

Requests use environment variables instead of hard-coded URLs.

Example:

`{{baseUrl}}/{{employeeBase}}`

which resolves to:

`http://localhost:8080/api/v1/employees`

---

# 12. Postman Test Scripts

Postman test scripts are used to automatically validate API responses.

## 12.1 Status Code Validation

Example:

    pm.test("Status code is 200 OK", function () {
        pm.response.to.have.status(200);
    });

For employee creation:

    pm.test("Status code is 201 Created", function () {
        pm.response.to.have.status(201);
    });

For employee deletion:

    pm.test("Status code is 204 No Content", function () {
        pm.response.to.have.status(204);
    });

---

# 13. Response Schema Validation

The Postman collection also validates the structure of JSON responses.

Employee response schemas validate fields such as:

- `department`
- `email`
- `firstName`
- `id`
- `lastName`
- `salary`

Account response schemas validate fields such as:

- `accountHolder`
- `balance`
- `id`

Schema validation helps ensure that the API responses have the expected structure and data types.

---

# 14. Newman CLI Testing

The Postman collection can be executed from the command line using Newman.

Newman allows the Postman collection to run without opening the Postman application.

### Newman Version

The Newman version used during testing is:

`6.2.2`

### Collection File

`API Testing.postman_collection.json`

### Environment File

`API Testing Env.postman_environment.json`

### Newman Command

From the directory containing the exported collection and environment files:

    newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"

The command executes all requests in the collection and runs the associated pre-request and test scripts.

Newman reports:

- Number of requests executed
- HTTP status codes
- Test-script results
- Pre-request script results
- Assertion results
- Failed tests
- Response times
- Total execution duration
- Data received

Example successful summary:

    requests    8
    failed      0

The Newman execution is used to verify that the APIs and their automated Postman tests work correctly from the command line.

---

# 15. API Test Coverage

The Postman/Newman collection covers the following APIs.

## Employee APIs

| Operation | Method | Endpoint | Expected Status |
|---|---|---|---|
| Get all employees | GET | `/api/v1/employees` | 200 |
| Get employee by ID | GET | `/api/v1/employees/{id}` | 200 |
| Add employee | POST | `/api/v1/employees` | 201 |
| Update employee | PUT | `/api/v1/employees/{id}` | 200 |
| Delete employee | DELETE | `/api/v1/employees/{id}` | 204 |

## Account APIs

| Operation | Method | Endpoint | Expected Status |
|---|---|---|---|
| Deposit amount | POST | `/api/v1/accounts/{id}/deposit` | 200 |
| Withdraw amount | POST | `/api/v1/accounts/{id}/withdraw` | 200 |
| Get balance | GET | `/api/v1/accounts/{id}/balance` | 200 |

---

# 16. API Access and Security

The current Spring Security configuration intentionally leaves the Employee and Account APIs publicly accessible.

The following APIs do not require authentication:

- `/api/v1/employees/**`
- `/api/v1/accounts/**`

The Actuator endpoints are protected.

The protected Actuator URL pattern is:

`/actuator/**`

HTTP Basic Authentication is enabled for the protected Actuator endpoints.

Therefore, API clients can access Employee and Account APIs without Basic Authentication, while Actuator endpoints require authentication.

---

# 17. Example cURL Requests

The following examples can also be used to test the APIs directly from the command line.

## Get All Employees

    curl "http://localhost:8080/api/v1/employees"

## Get Employee By ID

    curl "http://localhost:8080/api/v1/employees/1"

## Create Employee

    curl -X POST "http://localhost:8080/api/v1/employees" ^
      -H "Content-Type: application/json" ^
      -d "{\"firstName\":\"Vamsi\",\"lastName\":\"Yalla\",\"email\":\"vamsi@example.com\",\"department\":\"IT\",\"salary\":50000}"

## Update Employee

    curl -X PUT "http://localhost:8080/api/v1/employees/1" ^
      -H "Content-Type: application/json" ^
      -d "{\"firstName\":\"Vamsi Pavan\",\"lastName\":\"Yalla\",\"email\":\"vamsi@example.com\",\"department\":\"IT\",\"salary\":55000}"

## Delete Employee

    curl -X DELETE "http://localhost:8080/api/v1/employees/1"

## Deposit Amount

    curl -X POST "http://localhost:8080/api/v1/accounts/1/deposit" ^
      -H "Content-Type: application/json" ^
      -d "{\"amount\":1000}"

## Withdraw Amount

    curl -X POST "http://localhost:8080/api/v1/accounts/1/withdraw" ^
      -H "Content-Type: application/json" ^
      -d "{\"amount\":1000}"

## Get Account Balance

    curl "http://localhost:8080/api/v1/accounts/1/balance"

---

# 18. Related Documentation

Additional technical documentation is available in the `docs` directory.

- [Validation and Exception Handling](validation-and-exception-handling.md)
- [Database and HikariCP](database-and-hikaricp.md)
- [Caching and Redis](caching-and-redis.md)
- [Postman and Newman Testing](postman-newman-testing.md)
- [Actuator and Monitoring](actuator-monitoring.md)
- [Security](security.md)

---

# 19. Summary

The Employee Management application provides REST APIs for:

- Employee creation
- Employee retrieval
- Employee update
- Employee deletion
- Employee pagination
- Account deposits
- Account withdrawals
- Account balance retrieval
- Request validation
- Centralized exception handling
- Duplicate email handling
- Business rule validation
- API response schema validation

Postman is used for API testing and automated assertions.

The Postman collection is exported for team sharing and can be executed through Newman CLI.

The Newman command provides command-line execution of the complete API test collection, including pre-request scripts, API requests, status-code assertions, and response schema validation.