# Postman and Newman API Testing

## 1. Overview

The Employee Management application APIs were tested using **Postman** and **Newman**.

The Postman collection contains API requests for:

- Employee management
- Account/banking operations
- Response status validation
- Response schema validation
- Environment variables
- Pre-request scripts
- Request chaining using variables

The collection was exported and executed from the command line using Newman.

---

## 2. Tools Used

| Tool | Purpose |
|---|---|
| Postman | Create and manually execute API requests |
| Postman Collection | Organize and store API test requests |
| Postman Environment | Store reusable environment variables |
| Postman Scripts | Automate authentication and response validation |
| Newman | Execute the Postman collection from the command line |
| Node.js | Runtime required for Newman |
| Spring Boot | Backend application providing the REST APIs |

---

## 3. Postman Collection Structure

The collection is named:

`API Testing`

The requests are organized by feature/module.

```text
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
```

This organization keeps related API requests together and makes the collection easier to execute and maintain.

---

## 4. Employee API Requests

The Employee module contains the following requests:

| Operation | Method | Endpoint | Expected Status |
|---|---|---|---|
| Get all Employees | GET | `/api/v1/employees` | 200 |
| Get Employee by ID | GET | `/api/v1/employees/{id}` | 200 |
| Add Employee | POST | `/api/v1/employees` | 201 |
| Update Employee | PUT | `/api/v1/employees/{id}` | 200 |
| Delete Employee | DELETE | `/api/v1/employees/{id}` | 204 |

The requests use environment variables instead of hard-coded URLs.

For example:

```text
{{baseUrl}}/{{employeeBase}}
```

and:

```text
{{baseUrl}}/{{employeeBase}}/{{employeeId}}
```

---

## 5. Account API Requests

The Account module contains the following requests:

| Operation | Method | Endpoint | Expected Status |
|---|---|---|---|
| Deposit Amount | POST | `/api/v1/accounts/{id}/deposit` | 200 |
| Withdrawl Amount | POST | `/api/v1/accounts/{id}/withdraw` | 200 |
| Get Balance | GET | `/api/v1/accounts/{id}/balance` | 200 |

The account requests also use environment variables.

For example:

```text
{{baseUrl}}/{{accountBase}}/{{accountId}}/deposit
```

```text
{{baseUrl}}/{{accountBase}}/{{accountId}}/withdraw
```

```text
{{baseUrl}}/{{accountBase}}/{{accountId}}/balance
```

---

## 6. Postman Environment

An environment named:

`API Testing Env`

is used for storing reusable variables.

The environment contains variables such as:

```text
baseUrl
employeeId
accountId
token
employeeBase
accountBase
username
passwd
```

The main environment configuration used for local testing is:

```text
baseUrl = http://localhost:8080
```

The API base paths are represented using variables such as:

```text
employeeBase = api/v1/employees
accountBase = api/v1/accounts
```

Using environment variables allows the same collection to be used against different environments without changing every request.

For example:

```text
{{baseUrl}}/{{employeeBase}}
```

resolves to:

```text
http://localhost:8080/api/v1/employees
```

---

## 7. Authentication and Pre-request Script

A collection-level pre-request script was configured in Postman.

The script runs before each request in the collection.

The configured script adds an Authorization header:

```javascript
pm.environment.set("token", "token-value");

pm.request.headers.add({
    key: "Authorization",
    value: "Bearer " + pm.environment.get("token")
});
```

This demonstrates the use of a collection-level pre-request script for common request configuration.

### Important Security Note

The current Employee and Account APIs are publicly accessible according to the Spring Security configuration.

Only Actuator endpoints are protected using HTTP Basic Authentication.

Therefore, the Authorization header added by the current Postman pre-request script is not required for the Employee and Account APIs.

The protected Actuator endpoints require Basic Authentication.

---

## 8. Spring Security Configuration

The application uses Spring Security with HTTP Basic authentication.

The security configuration allows public access to normal application endpoints while requiring authentication for Actuator endpoints.

The relevant configuration is:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").authenticated()
    .anyRequest().permitAll()
)
.httpBasic(Customizer.withDefaults());
```

Therefore:

```text
/api/v1/employees/**       -> Public
/api/v1/accounts/**        -> Public
/actuator/**               -> Authentication required
```

The Postman Employee and Account requests can therefore be executed without Basic Authentication.

---

## 9. Test Scripts

Postman test scripts were added to validate API responses automatically.

The tests verify expected HTTP status codes.

Examples include:

```javascript
pm.test("Status code is 200 OK", function () {
    pm.response.to.have.status(200);
});
```

For employee creation:

```javascript
pm.test("Status code is 201 Created", function () {
    pm.response.to.have.status(201);
});
```

For deletion:

```javascript
pm.test("Status code is 204 No Content", function () {
    pm.response.to.have.status(204);
});
```

These tests ensure that the API returns the expected HTTP status code.

---

## 10. Response Schema Validation

Postman JSON schema validation is used to verify the structure of API responses.

For example, an Employee response is expected to contain fields such as:

```text
department
email
firstName
id
lastName
salary
```

A schema validation test is performed using:

```javascript
pm.test("Employee response schema is valid", function () {
    pm.response.to.have.jsonSchema(schema);
});
```

This verifies not only that the API returns a response, but also that the response follows the expected JSON structure.

---

## 11. Employee Response Schema

The Employee response schema validates the following fields:

```text
department  -> string
email       -> string
firstName   -> string
id          -> integer
lastName    -> string
salary      -> number
```

For paginated Employee responses, the response also contains pagination-related fields:

```text
content
totalPages
totalElements
size
number
numberOfElements
first
last
empty
```

The `content` property contains the Employee objects.

---

## 12. Account Response Schema

The Deposit and Withdraw operations return account information containing:

```text
accountHolder
balance
id
```

The response is validated using JSON schema validation.

Example:

```javascript
pm.test("Balance response schema is valid", function () {
    pm.response.to.have.jsonSchema(schema);
});
```

The balance endpoint is also validated against the expected response structure.

---

## 13. Request Variables and Chaining

Postman environment variables are used to avoid hard-coding values.

For example:

```text
employeeId
accountId
```

can be used in request URLs:

```text
{{baseUrl}}/{{employeeBase}}/{{employeeId}}
```

and:

```text
{{baseUrl}}/{{accountBase}}/{{accountId}}/balance
```

This makes it easier to test different Employee and Account records.

The collection can also be extended to automatically store IDs from previous API responses and use them in subsequent requests.

---

## 14. Manual Postman Testing

Before using Newman, the collection was executed from Postman.

The Postman runner was used to execute the requests and verify:

- HTTP status codes
- Response schemas
- Request execution
- Pre-request scripts
- Test scripts
- Environment variables

Successful assertions are displayed in Postman as passed tests.

---

## 15. Exporting the Collection

The Postman collection was exported as:

```text
API Testing.postman_collection.json
```

The environment was exported as:

```text
API Testing Env.postman_environment.json
```

These JSON files allow the API test collection to be shared and executed outside the Postman GUI.

The exported collection can be stored in the project repository for team usage.

---

## 16. Newman

**Newman** is the command-line collection runner for Postman.

It allows the exported Postman collection to be executed without opening the Postman application.

This is useful for:

- Command-line testing
- CI/CD pipelines
- Automated API testing
- Build verification
- Regression testing
- Team environments

---

## 17. Newman Installation

Node.js was installed first.

The Newman version used for this project is:

```text
6.2.2
```

The Newman installation can be verified using:

```bash
newman --version
```

Expected output:

```text
6.2.2
```

---

## 18. Running the Collection with Newman

The exported collection and environment files are placed in the working directory.

Example:

```text
Newman/
|
+-- API Testing.postman_collection.json
+-- API Testing Env.postman_environment.json
```

The collection is executed using:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

The `run` command tells Newman to execute the Postman collection.

The `-e` option specifies the Postman environment file.

---

## 19. Newman Execution

During execution, Newman displays each request and its result.

Example:

```text
newman

API Testing

Employee
  Get all Employees
  GET http://localhost:8080/api/v1/employees [200 OK]

  √ Status code is 200 OK
  √ Employee page response schema is valid

Account
  Deposit Amount
  POST http://localhost:8080/api/v1/accounts/1/deposit [200 OK]

  √ Status code is 200 OK
  √ Balance response schema is valid
```

Newman also provides a final execution summary.

---

## 20. Understanding Newman Results

A successful execution reports information such as:

```text
iterations
requests
test-scripts
prerequest-scripts
assertions
total run duration
total data received
average response time
```

For example:

```text
requests        8
test-scripts    16
prerequest-scripts 8
assertions      15
```

The important value is the number of failed assertions.

A successful test execution should have:

```text
failed = 0
```

for the relevant assertions.

---

## 21. Handling Failed Tests

Newman reports failed assertions separately.

For example:

```text
AssertionError
Status code is 201 Created
expected response to have status code 201 but got 409
```

This means the API request itself was successfully sent, but the API returned a different status code than the test expected.

For example:

```text
Expected: 201 Created
Actual:   409 Conflict
```

The Newman test is therefore correctly identifying a problem with the test data or API behavior.

---

## 22. Duplicate Employee Scenario

The Employee creation API can return:

```text
409 Conflict
```

when an Employee with the same email already exists.

For example, if the collection repeatedly sends the same email:

```json
{
    "firstName": "Vamsi",
    "lastName": "Yalla",
    "email": "data7@gmail.com",
    "department": "IT",
    "salary": 50000
}
```

the first request may create the Employee successfully:

```text
201 Created
```

A subsequent request with the same email can return:

```text
409 Conflict
```

If the Postman test expects `201`, Newman will correctly mark that assertion as failed.

This is a test-data issue rather than a Newman installation issue.

---

## 23. Test Data Considerations

When running the collection repeatedly, the test data should account for database state.

For example, an Employee creation request using a fixed email may succeed once and fail on later executions because the email already exists.

For repeatable automated testing, test data should be designed so that:

- Employee emails are unique when testing creation
- Existing Employee IDs are used for GET, PUT, and DELETE operations
- Existing Account IDs are used for banking operations
- Requests are executed in an appropriate order
- Test data is not accidentally reused when uniqueness is required

---

## 24. Recommended Employee Test Flow

A logical Employee test flow is:

```text
Get all Employees
       |
       v
Create Employee
       |
       v
Store returned Employee ID
       |
       v
Get Employee by ID
       |
       v
Update Employee
       |
       v
Delete Employee
```

This approach reduces dependency on hard-coded IDs.

The created Employee ID can be stored in an environment variable and reused by subsequent requests.

---

## 25. Recommended Account Test Flow

A logical Account test flow is:

```text
Existing Account
      |
      v
Deposit Amount
      |
      v
Withdraw Amount
      |
      v
Get Balance
```

The Account ID can be stored in:

```text
{{accountId}}
```

and reused by the Account requests.

---

## 26. Newman and Spring Boot

Before executing the Newman collection, the Spring Boot application must be running.

The APIs are available at:

```text
http://localhost:8080
```

The Newman execution therefore sends requests to the locally running Spring Boot application.

Example:

```text
Newman
   |
   | HTTP request
   v
Spring Boot Application
   |
   v
Controller
   |
   v
Service
   |
   v
Database / Redis
```

---

## 27. Newman and Database

The Employee APIs use the application's configured Microsoft SQL Server database.

Therefore, when Newman executes Employee requests, the Spring Boot application must be able to connect to the configured database.

The database must be available before executing tests that require database access.

---

## 28. Newman and Redis

The application uses Redis caching.

Redis is running through a Docker container.

The Spring Boot application connects to Redis using:

```text
host = localhost
port = 6379
```

When Newman executes read operations, the request may interact with the application's caching layer depending on the service implementation.

The Newman tests validate the API response rather than directly testing Redis internals.

---

## 29. Newman and Actuator

Actuator endpoints are separate from the Employee and Account API tests.

The application protects:

```text
/actuator/**
```

using HTTP Basic Authentication.

Examples of Actuator endpoints include:

```text
/actuator/health
/actuator/metrics
/actuator/info
/actuator/caches
```

These endpoints require authentication according to the application's Spring Security configuration.

---

## 30. Testing Protected Actuator Endpoints

For protected Actuator endpoints, Postman can be configured with:

```text
Authorization Type: Basic Auth
Username: root
Password: <configured-password>
```

The same authentication can be supplied to Newman when required.

For example, authentication details can be configured through the Postman request or environment.

Sensitive credentials should not be committed to source control.

---

## 31. Newman Command for the Environment

The complete command used for this project is:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

The collection file contains:

```text
API requests
Test scripts
Pre-request scripts
Collection structure
Schema validation
```

The environment file contains:

```text
Environment variables
```

---

## 32. Running Newman from the Project Directory

Example Windows command prompt:

```bash
cd C:\path\to\project\Newman
```

Then:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

Newman then executes the entire collection.

---

## 33. Successful Newman Execution

A successful execution should show passing assertions such as:

```text
√ Status code is 200 OK
√ Employee page response schema is valid
√ Employee response schema is valid
√ Balance response schema is valid
```

The final summary should show no failed assertions for the scenarios being tested.

Example:

```text
requests    8
failed      0
```

---

## 34. API Test Coverage

The Postman collection provides automated testing for:

### Employee APIs

- Retrieve all Employees
- Retrieve Employee by ID
- Create Employee
- Update Employee
- Delete Employee

### Account APIs

- Deposit money
- Withdraw money
- Retrieve account balance

### Validation

- HTTP status code validation
- JSON response schema validation
- API response structure validation

### Automation

- Environment variables
- Pre-request scripts
- Test scripts
- Collection export
- Newman command-line execution

---

## 35. Project Test Execution Flow

The overall API testing process is:

```text
Start Spring Boot Application
          |
          v
Ensure SQL Server is Available
          |
          v
Ensure Redis Docker Container is Running
          |
          v
Open / Prepare Postman Collection
          |
          v
Configure API Testing Environment
          |
          v
Run Collection in Postman
          |
          v
Verify Test Assertions
          |
          v
Export Collection and Environment
          |
          v
Run Collection Using Newman
          |
          v
Review Newman Test Report
```

---

## 36. Purpose of Newman in This Project

Newman completes the command-line API testing requirement of the project.

The Postman collection is used to define the API tests, while Newman provides a way to execute those tests outside the Postman GUI.

This makes the API test suite suitable for repeatable execution and future CI/CD integration.

---

## 37. Summary

The project uses Postman to create and organize API tests for the Employee and Account modules.

The collection includes:

- Employee API requests
- Account API requests
- Environment variables
- Pre-request scripts
- Status code assertions
- JSON schema validation
- Request variables
- Collection-level configuration

The collection and environment are exported as JSON files and executed using Newman.

The main Newman command is:

```bash
newman run "API Testing.postman_collection.json" -e "API Testing Env.postman_environment.json"
```

This provides a command-line based API testing workflow for the Spring Boot application.