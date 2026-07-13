# Enterprise Payment API

A backend payment processing REST API built with Java and Spring Boot, demonstrating a clean layered architecture, PostgreSQL persistence, Docker-based local infrastructure, request validation, and centralized exception handling.

## Features

- Health check endpoint
- Create a payment
- Retrieve a payment by ID
- Retrieve all payments
- Duplicate transaction reference validation
- Request validation
- Centralized exception handling
- PostgreSQL persistence using Spring Data JPA
- Docker Compose setup for PostgreSQL
- UUID-based payment identifiers
- Automatic creation and update timestamps

## Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- Jakarta Validation
- PostgreSQL 16
- Docker
- Docker Compose
- Maven

## Project Structure

```text
src/main/java/com/vinitjoshi/payment
├── config
│   └── SecurityConfig.java
├── controller
│   ├── HealthController.java
│   └── PaymentController.java
├── dto
│   ├── CreatePaymentRequest.java
│   └── PaymentResponse.java
├── entity
│   ├── Payment.java
│   └── PaymentStatus.java
├── exception
│   ├── DuplicatePaymentException.java
│   ├── GlobalExceptionHandler.java
│   └── PaymentNotFoundException.java
├── repository
│   └── PaymentRepository.java
├── service
│   └── PaymentService.java
└── EnterprisePaymentApiApplication.java
```

## Prerequisites

Before running the application, make sure the following are installed:

- Java
- Docker Desktop
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

## Running PostgreSQL with Docker

Start PostgreSQL from the project root directory:

```bash
docker compose up -d
```

Verify that the container is running:

```bash
docker compose ps
```

The PostgreSQL container should show a `healthy` status.

To stop the container:

```bash
docker compose down
```

## Database Configuration

The application connects to PostgreSQL using the following local development configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/paymentdb
spring.datasource.username=paymentuser
spring.datasource.password=paymentpass
```

The PostgreSQL database is provisioned using `compose.yaml`.

> Note: The credentials in this repository are intended for local development only. Production environments should use environment variables or a secrets management solution.

## Running the Application

### Using Maven Wrapper on Windows

```bash
mvnw.cmd spring-boot:run
```

### Using Maven Wrapper on Linux or macOS

```bash
./mvnw spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

## API Endpoints

### Health Check

```http
GET /api/health
```

Example response:

```json
{
  "status": "UP",
  "timestamp": "2026-07-13T12:03:10.825779700Z",
  "application": "Enterprise Payment API",
  "version": "1.0.0"
}
```

---

### Create Payment

```http
POST /api/payments
```

Example request:

```json
{
  "transactionReference": "TXN-20260713-001",
  "amount": 125.50,
  "currency": "EUR",
  "customerId": "CUST-001",
  "description": "Test payment"
}
```

Example response:

```json
{
  "id": "3b9e3e41-781f-42d7-8409-3e7f62f2f786",
  "transactionReference": "TXN-20260713-001",
  "amount": 125.50,
  "currency": "EUR",
  "status": "PENDING",
  "customerId": "CUST-001",
  "description": "Test payment",
  "createdAt": "2026-07-13T12:40:34.381682Z",
  "updatedAt": "2026-07-13T12:40:34.381682Z"
}
```

Successful requests return:

```text
201 Created
```

---

### Get All Payments

```http
GET /api/payments
```

Example response:

```json
[
  {
    "id": "3b9e3e41-781f-42d7-8409-3e7f62f2f786",
    "transactionReference": "TXN-20260713-001",
    "amount": 125.50,
    "currency": "EUR",
    "status": "PENDING",
    "customerId": "CUST-001",
    "description": "Test payment",
    "createdAt": "2026-07-13T12:40:34.381682Z",
    "updatedAt": "2026-07-13T12:40:34.381682Z"
  }
]
```

---

### Get Payment by ID

```http
GET /api/payments/{id}
```

Example:

```text
GET /api/payments/3b9e3e41-781f-42d7-8409-3e7f62f2f786
```

Successful requests return:

```text
200 OK
```

If the payment does not exist:

```text
404 Not Found
```

---

## Error Handling

The API provides centralized exception handling for common errors.

### Duplicate Transaction Reference

A transaction reference must be unique.

Attempting to create another payment using an existing transaction reference returns:

```text
409 Conflict
```

### Validation Error

Invalid request data returns:

```text
400 Bad Request
```

### Payment Not Found

Requesting a payment with an unknown ID returns:

```text
404 Not Found
```

## Current Architecture

```text
Client
   |
   v
REST Controller
   |
   v
Service Layer
   |
   v
Spring Data JPA Repository
   |
   v
PostgreSQL
   |
   v
Docker Container
```

The application follows a layered architecture to separate API, business logic, and persistence concerns.

## Development Roadmap

Planned enhancements include:

- Payment status update workflow
- Additional unit and integration tests
- Testcontainers-based PostgreSQL integration testing
- JWT authentication and authorization
- OpenAPI / Swagger documentation
- Dockerization of the Spring Boot application
- CI/CD pipeline
- Improved configuration using environment variables
- Cloud deployment

## Author

**Vinit Joshi**

Senior Java Developer / Technical Lead

Technologies: Java, Spring Boot, Microservices, Kafka, PostgreSQL, Docker, Kubernetes, AWS, and GCP