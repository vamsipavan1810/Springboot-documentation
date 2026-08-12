# Validation and Exception Handling

## 1. Overview

The Employee Management application implements request validation and centralized exception handling for its REST APIs.

The main objectives are:

- Validate incoming request data
- Return appropriate HTTP status codes
- Provide a consistent error response format
- Handle application-specific exceptions
- Handle validation failures
- Handle duplicate resources
- Handle business rule violations
- Handle unexpected exceptions
- Provide user-friendly error messages
- Avoid exposing internal implementation details
- Log exceptions appropriately

Exception handling is centralized using Spring's `@RestControllerAdvice`.

---

## 2. Validation

Request validation ensures that incoming API data satisfies the required constraints before it is processed.

Validation is applied to request data such as employee details and account transaction amounts.

Common validation failures include:

- Missing required fields
- Invalid field values
- Invalid email addresses
- Invalid salary values
- Invalid transaction amounts
- Other constraints defined by the application

Invalid input results in:

**HTTP 400 Bad Request**

---

## 3. Validation Flow

The general validation flow is:

    Client Request
          |
          v
    Controller
          |
          v
    Request Validation
          |
          +---- Valid ----> Service Layer
          |
          +---- Invalid --> GlobalExceptionHandler
                                |
                                v
                           ErrorResponse
                                |
                                v
                         HTTP 400 Response

---

## 4. Global Exception Handling

The application uses Spring's `@RestControllerAdvice` to centralize exception handling.

Instead of handling exceptions separately inside every controller, exceptions are processed by a common global exception handler.

This provides:

- Consistent error responses
- Centralized exception processing
- Cleaner controller classes
- Easier maintenance
- Better API client experience

The general flow is:

    Controller
        |
        v
    Exception occurs
        |
        v
    GlobalExceptionHandler
        |
        v
    Create ErrorResponse
        |
        v
    Return appropriate HTTP status

---

## 5. Exception Types

The application handles different types of exceptions based on the situation.

| Exception / Situation | HTTP Status | Purpose |
|---|---:|---|
| `ResourceNotFoundException` | 404 | Requested resource does not exist |
| `MethodArgumentNotValidException` | 400 | Request validation failed |
| `DuplicateResourceException` | 409 | Duplicate resource detected |
| Business rule violation | 422 | Request violates a business rule |
| Generic `Exception` | 500 | Unexpected application error |

---

## 6. ResourceNotFoundException

`ResourceNotFoundException` is used when a requested resource does not exist.

Examples include:

- Employee ID does not exist
- Account ID does not exist
- Requested resource has already been removed

The API returns:

**HTTP 404 Not Found**

Example:

    GET /api/v1/employees/999

Example response:

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 404,
      "message": "Employee not found",
      "errors": [],
      "path": "/api/v1/employees/999"
    }

---

## 7. MethodArgumentNotValidException

`MethodArgumentNotValidException` occurs when request validation fails.

For example, an employee creation request may contain invalid or missing values.

The global exception handler processes the validation errors and returns:

**HTTP 400 Bad Request**

Example response:

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

The validation messages help the client understand what needs to be corrected.

---

## 8. DuplicateResourceException

`DuplicateResourceException` is used when an operation attempts to create a resource that already exists.

A common example is duplicate employee email.

For example, if an employee already exists with a particular email and another employee is created using the same email, the application returns:

**HTTP 409 Conflict**

Example response:

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 409,
      "message": "Employee with email already exists",
      "errors": [],
      "path": "/api/v1/employees"
    }

This allows clients to distinguish duplicate resources from invalid request data.

---

## 9. Business Rule Violations

Business rule violations are different from basic input validation.

A request can contain valid data types and still violate a rule defined by the application's business logic.

Examples include:

- Attempting to withdraw an invalid amount
- Attempting an operation that is not allowed for an account
- Violating an application-specific business rule

Business rule violations can be represented using:

**HTTP 422 Unprocessable Entity**

Example response:

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 422,
      "message": "Business rule validation failed",
      "errors": [],
      "path": "/api/v1/accounts/1/withdraw"
    }

The response should provide a meaningful message explaining why the operation cannot be completed.

---

## 10. Generic Exception Handling

Unexpected application exceptions are handled by the generic exception handler.

This prevents internal implementation details from being directly returned to API clients.

Unexpected errors result in:

**HTTP 500 Internal Server Error**

Example response:

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 500,
      "message": "An unexpected error occurred",
      "errors": [],
      "path": "/api/v1/employees"
    }

The API should not expose stack traces or internal exception information to clients.

---

## 11. ErrorResponse

The application uses an `ErrorResponse` record to provide a consistent structure for API errors.

The response contains:

| Field | Description |
|---|---|
| `timestamp` | Time when the error occurred |
| `status` | HTTP status code |
| `message` | User-friendly error message |
| `errors` | Validation or additional error messages |
| `path` | API endpoint that generated the error |

Example structure:

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 400,
      "message": "Validation failed",
      "errors": [
        "Invalid request field"
      ],
      "path": "/api/v1/employees"
    }

A consistent error structure makes it easier for API clients to process errors.

---

## 12. HTTP Status Code Strategy

The application follows the following status-code strategy.

| Status Code | Meaning | Example |
|---:|---|---|
| 200 | OK | Successful GET, update, or account operation |
| 201 | Created | Employee successfully created |
| 204 | No Content | Employee successfully deleted |
| 400 | Bad Request | Invalid request input |
| 404 | Not Found | Employee or account does not exist |
| 409 | Conflict | Duplicate employee email |
| 422 | Unprocessable Entity | Business rule violation |
| 500 | Internal Server Error | Unexpected application error |

---

## 13. Validation Error Handling

When request validation fails, the application collects the relevant validation messages and includes them in the `errors` field.

Example:

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

This provides the API consumer with useful information without exposing internal framework details.

---

## 14. User-Friendly Error Messages

The API uses meaningful error messages instead of exposing raw internal exceptions.

For example, an internal database or framework exception should not normally be returned directly to the client.

Instead of exposing an implementation-specific error, the API can return a message such as:

    Employee with email already exists

This makes the API easier to understand and safer to consume.

---

## 15. Protection of Internal Details

The application should not expose sensitive implementation information through API responses.

The following information should not be returned to API clients:

- Java stack traces
- Database credentials
- Database connection details
- Internal package names
- Internal class names
- SQL statements
- Server configuration
- Framework implementation details
- Other sensitive application information

Unexpected errors should return a generic message while detailed information can be recorded in application logs.

---

## 16. Exception Logging

Exceptions should be logged using appropriate logging levels.

Expected application-level problems can be logged at suitable informational or warning levels.

Unexpected application failures should be logged at the error level.

Logging allows developers to investigate failures without exposing internal information to API consumers.

General flow:

    Exception Occurs
          |
          v
    GlobalExceptionHandler
          |
          +---- Log Exception
          |
          v
    Create ErrorResponse
          |
          v
    Return HTTP Response

---

## 17. Employee API Validation Scenarios

The Employee API supports the following important validation and exception scenarios.

### 17.1 Valid Employee Creation

A valid employee request should create a new employee.

Expected status:

**HTTP 201 Created**

### 17.2 Invalid Employee Request

If the employee request contains invalid or missing required information:

Expected status:

**HTTP 400 Bad Request**

### 17.3 Duplicate Employee Email

If an employee already exists with the same email address:

Expected status:

**HTTP 409 Conflict**

### 17.4 Employee Not Found

If an employee with the requested ID does not exist:

Expected status:

**HTTP 404 Not Found**

This can apply to:

- Get employee
- Update employee
- Delete employee

---

## 18. Account API Validation Scenarios

The Account API handles validation and resource-related errors.

### 18.1 Deposit

A valid deposit request should be processed successfully.

Expected status:

**HTTP 200 OK**

### 18.2 Withdrawal

A valid withdrawal request should be processed successfully.

Expected status:

**HTTP 200 OK**

If the withdrawal violates an applicable business rule, an appropriate error status should be returned.

### 18.3 Account Not Found

If the requested account does not exist:

Expected status:

**HTTP 404 Not Found**

---

## 19. API Error Response Examples

### 19.1 400 Bad Request

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 400,
      "message": "Validation failed",
      "errors": [
        "Invalid employee data"
      ],
      "path": "/api/v1/employees"
    }

### 19.2 404 Not Found

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 404,
      "message": "Employee not found",
      "errors": [],
      "path": "/api/v1/employees/999"
    }

### 19.3 409 Conflict

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 409,
      "message": "Employee with email already exists",
      "errors": [],
      "path": "/api/v1/employees"
    }

### 19.4 422 Unprocessable Entity

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 422,
      "message": "Business rule violation",
      "errors": [],
      "path": "/api/v1/accounts/1/withdraw"
    }

### 19.5 500 Internal Server Error

    {
      "timestamp": "2026-08-12T10:30:00",
      "status": 500,
      "message": "An unexpected error occurred",
      "errors": [],
      "path": "/api/v1/employees"
    }

---

## 20. Exception Handling Flow

The overall exception-handling flow can be represented as:

    API Request
         |
         v
    Controller
         |
         v
    Request Validation
         |
         +-----------------------+
         |                       |
       Valid                  Invalid
         |                       |
         v                       v
    Service Layer       Validation Exception
         |                       |
         |                       v
         |              GlobalExceptionHandler
         |                       |
         |                       v
         |                   HTTP 400
         |
         +-------------------------------+
         |               |               |
         v               v               v
    Resource Not     Duplicate       Unexpected
       Found          Resource         Exception
         |               |               |
         v               v               v
     HTTP 404        HTTP 409        HTTP 500
         |
         +---------------+
                         |
                         v
                   ErrorResponse
                         |
                         v
                       Client

---

## 21. Postman Testing

The application's validation and exception-handling behavior is tested using Postman.

The Postman collection contains requests for the Employee and Account APIs.

The tests verify:

- HTTP status codes
- Response structure
- Response schema
- Successful operations
- Resource-not-found scenarios
- Duplicate-resource scenarios
- Validation scenarios

Example test scenarios:

| Test Scenario | Expected Status |
|---|---:|
| Get all employees | 200 |
| Get existing employee | 200 |
| Get non-existing employee | 404 |
| Create employee | 201 |
| Create duplicate employee | 409 |
| Update existing employee | 200 |
| Update non-existing employee | 404 |
| Delete existing employee | 204 |
| Delete non-existing employee | 404 |
| Deposit amount | 200 |
| Withdraw amount | 200 |
| Get account balance | 200 |
| Get non-existing account | 404 |

---

## 22. Postman Schema Validation

The Postman collection validates response schemas for successful API responses.

For Employee API responses, the expected fields include:

- `department`
- `email`
- `firstName`
- `id`
- `lastName`
- `salary`

For Account API responses, the expected fields include:

- `accountHolder`
- `balance`
- `id`

Schema validation verifies that successful responses contain the expected structure and data types.

---

## 23. Newman Testing

The Postman collection can be executed from the command line using Newman.

Example command:

    newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"

The environment file contains variables such as:

    baseUrl
    employeeId
    accountId
    employeeBase
    accountBase
    username
    passwd
    token

Newman executes the collection outside the Postman application.

It runs:

- API requests
- Pre-request scripts
- Test scripts
- Status-code assertions
- Response schema validation
- Environment-variable resolution

---

## 24. Example Newman Result

A successful Newman execution reports the number of executed requests and assertions.

Example:

    requests                 8
    failed                   0

    test-scripts             16
    prerequest-scripts       8

A failed assertion identifies the specific request and test that failed.

For example:

    AssertionError
    expected response to have status code 201 but got 409

This helps identify an API behavior that does not match the expected result.

---

## 25. Testing Duplicate Employee Creation

One of the important validation scenarios is duplicate employee creation.

If an employee is created using an email address that already exists, the API may return:

**409 Conflict**

The Postman/Newman test can verify that the API returns the expected status code.

If the test expects `201 Created` but the API returns `409 Conflict`, Newman correctly reports the assertion as failed.

This can indicate that:

- The test data already exists
- The expected status code is incorrect
- The API behavior needs investigation

The test result should therefore be interpreted together with the actual API response.

---

## 26. Testing Not-Found Scenarios

Resource-not-found scenarios can be tested by using an ID that does not exist.

Example:

    GET /api/v1/employees/9999

Expected response:

    404 Not Found

The same approach can be used for update and delete operations.

---

## 27. Benefits of Centralized Exception Handling

Centralized exception handling provides several benefits:

- Consistent API responses
- Cleaner controller code
- Centralized error processing
- Easier maintenance
- Better client-side error handling
- Improved debugging
- Reduced code duplication
- Safer error responses
- Clear HTTP status codes

---

## 28. Separation of Concerns

The application follows a separation-of-concerns approach.

    Controller
        |
        | Handles HTTP requests
        v
    Service
        |
        | Handles business logic
        v
    Repository
        |
        | Handles data access
        v
    Database

Exception handling is handled separately:

    Controller / Service / Repository
                  |
                  v
           Exception occurs
                  |
                  v
        GlobalExceptionHandler
                  |
                  v
            ErrorResponse
                  |
                  v
            HTTP Response

This keeps business logic separate from HTTP error-response handling.

---

## 29. Security Considerations

Error responses should not expose sensitive information.

In particular, the API should never expose:

- Passwords
- Authentication credentials
- Database credentials
- Internal server information
- Stack traces
- SQL statements
- Redis configuration details
- Internal implementation details

Only information useful to the API consumer should be returned.

---

## 30. Testing Objectives

The validation and exception-handling tests should verify both successful and unsuccessful scenarios.

The main objectives are:

- Verify valid requests
- Verify invalid requests
- Verify missing resources
- Verify duplicate resources
- Verify business rule violations
- Verify correct HTTP status codes
- Verify error response structure
- Verify response schema
- Verify user-friendly messages
- Verify unexpected exceptions are handled safely

---

## 31. Summary

The Employee Management application uses centralized validation and exception handling to provide predictable REST API behavior.

The implementation covers:

- Request validation
- `@RestControllerAdvice`
- `ResourceNotFoundException`
- `MethodArgumentNotValidException`
- `DuplicateResourceException`
- Business rule violations
- Generic exception handling
- `ErrorResponse`
- HTTP 400 responses
- HTTP 404 responses
- HTTP 409 responses
- HTTP 422 responses
- HTTP 500 responses
- User-friendly error messages
- Exception logging
- Protection of internal implementation details
- Postman API testing
- Newman CLI testing
- Response schema validation

This approach provides a consistent, maintainable, and client-friendly error-handling strategy for the Employee Management REST APIs.