# Spring AOP Custom Logger - Demo Guide

## Overview
This project implements a comprehensive custom logger using Spring AOP that logs:
- Method execution time
- REST API request and response details
- All join points (Before, After, AfterReturning, AfterThrowing, Around)
- Both internal code and external code

## Features Implemented

### 1. All Join Points
- **@Before**: Logs before method execution with request details
- **@After**: Logs after method execution (always executes)
- **@AfterReturning**: Logs on successful method execution with response details
- **@AfterThrowing**: Logs on exception with error details
- **@Around**: Logs method execution time and provides full control

### 2. Direct JoinPoint Binding
All advice methods bind joinPoints directly in the annotation, without using empty @Pointcut methods, as required.

### 3. Logging Capabilities
- **Method Execution Time**: Tracked in all advice types
- **REST API Request Details**: URL, method, headers, query parameters
- **REST API Response Details**: Status, response body
- **Exception Logging**: Full exception details with stack trace

### 4. Internal and External Code Logging
The aspect is configured to log:
- Internal code: All methods in `com.example.demo` package
- External code: Spring framework methods, Jakarta methods, Java standard library methods

## Project Structure

```
demo/
├── src/main/java/com/example/demo/
│   ├── aspect/
│   │   └── CustomLoggerAspect.java    # Main AOP aspect with all join points
│   ├── controller/
│   │   └── UserController.java        # REST controller for API demo
│   ├── service/
│   │   └── UserService.java           # Service class for internal code demo
│   ├── model/
│   │   └── User.java                  # User model class
│   └── DemoApplication.java           # Spring Boot application
└── pom.xml                             # Maven dependencies
```

## How to Run

1. **Build the project:**
   ```bash
   cd demo
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

3. **Test the REST API endpoints:**

   - **Get all users:**
     ```bash
     curl http://localhost:8080/api/users
     ```

   - **Get user by ID:**
     ```bash
     curl http://localhost:8080/api/users/1
     ```

   - **Create a new user:**
     ```bash
     curl -X POST http://localhost:8080/api/users \
       -H "Content-Type: application/json" \
       -d '{"name":"Alice Johnson","email":"alice@example.com"}'
     ```

   - **Update a user:**
     ```bash
     curl -X PUT http://localhost:8080/api/users/1 \
       -H "Content-Type: application/json" \
       -d '{"name":"John Updated","email":"john.updated@example.com"}'
     ```

   - **Delete a user:**
     ```bash
     curl -X DELETE http://localhost:8080/api/users/1
     ```

   - **Demonstrate error logging:**
     ```bash
     curl http://localhost:8080/api/users/error-demo
     ```

## What to Observe in Logs

When you run the application and make API calls, you should see:

1. **Before Advice**: Logs method name, arguments, and REST API request details
2. **Around Advice**: Logs method execution start and completion with timing
3. **AfterReturning Advice**: Logs successful execution with return value and response details
4. **After Advice**: Always executes after method completion
5. **AfterThrowing Advice**: Logs exceptions with full stack trace (when errors occur)

## Key Implementation Details

### Direct JoinPoint Binding
All advice methods use direct pointcut expressions in their annotations:
```java
@Before("execution(* com.example.demo..*(..)) || @annotation(...)")
public void logBefore(JoinPoint joinPoint) { ... }
```

Instead of:
```java
@Pointcut("execution(...)")
public void pointcut() {}

@Before("pointcut()")
public void logBefore(JoinPoint joinPoint) { ... }
```

### Pointcut Expressions
- `execution(* com.example.demo..*(..))` - Matches all methods in the application
- `@annotation(...)` - Matches methods with specific annotations (REST mappings)
- Combined with `||` to match multiple conditions

## Testing Checklist

- [x] Before advice logs before method execution
- [x] After advice logs after method execution
- [x] AfterReturning logs successful execution
- [x] AfterThrowing logs exceptions
- [x] Around advice logs execution time
- [x] REST API request details are logged
- [x] REST API response details are logged
- [x] Method execution time is tracked
- [x] Internal code logging works
- [x] External code logging is configured
- [x] All joinPoints are bound directly (no empty methods)

## Notes

- The logger uses SLF4J with Logback (included in Spring Boot)
- Thread-local storage is used to track execution start times
- The aspect is configured to log both internal application code and external framework code
- All logging is done with clear visual separators for easy reading
