# Enterprise Payment API

A production-style REST API for managing payments, built with **Java 21**, **Spring Boot**, **PostgreSQL**, **Docker**, and **OpenAPI/Swagger**.

This project demonstrates enterprise backend development practices including layered architecture, input validation, exception handling, database persistence, automated testing, and interactive API documentation.

---

## Features

- Create a new payment
- Retrieve a payment by ID
- Retrieve all payments
- Update payment status
- Delete a payment
- Prevent duplicate transaction references
- Request validation
- Centralized exception handling
- PostgreSQL database persistence
- Docker-based local database setup
- Health check endpoint
- OpenAPI 3 documentation
- Interactive Swagger UI
- Automated service and controller tests

---

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL 16
- Docker & Docker Compose
- Jakarta Bean Validation
- Spring Security
- JUnit 5
- Mockito
- MockMvc
- OpenAPI 3 / Swagger UI
- Maven

---

## Architecture

The application follows a layered architecture:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
PostgreSQL
```

### Package Structure

```text
src/main/java/com/vinitjoshi/payment
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── EnterprisePaymentApiApplication.java
```

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/payments` | Create a new payment |
| `GET` | `/api/payments` | Retrieve all payments |
| `GET` | `/api/payments/{id}` | Retrieve a payment by ID |
| `PATCH` | `/api/payments/{id}/status` | Update payment status |
| `DELETE` | `/api/payments/{id}` | Delete a payment |
| `GET` | `/api/health` | Check application health |

---

## Payment Statuses

A payment can have one of the following statuses:

```text
PENDING
PROCESSING
COMPLETED
FAILED
CANCELLED
```

New payments are created with the default status:

```text
PENDING
```

---

## Create Payment

### Request

```http
POST /api/payments
Content-Type: application/json
```

```json
{
  "transactionReference": "TXN-20260714-001",
  "amount": 125.50,
  "currency": "EUR",
  "customerId": "CUST-001",
  "description": "Test payment"
}
```

### Successful Response

```http
201 Created
```

```json
{
  "id": "3b9e3e41-781f-42d7-8409-3e7f62f2f786",
  "transactionReference": "TXN-20260714-001",
  "amount": 125.50,
  "currency": "EUR",
  "status": "PENDING",
  "customerId": "CUST-001",
  "description": "Test payment",
  "createdAt": "2026-07-14T12:40:34.381682Z",
  "updatedAt": "2026-07-14T12:40:34.381682Z"
}
```

---

## Get All Payments

### Request

```http
GET /api/payments
```

### Successful Response

```http
200 OK
```

Returns a list of payments.

---

## Get Payment by ID

### Request

```http
GET /api/payments/{id}
```

### Successful Response

```http
200 OK
```

If the payment does not exist, the API returns:

```http
404 Not Found
```

---

## Update Payment Status

### Request

```http
PATCH /api/payments/{id}/status
Content-Type: application/json
```

```json
{
  "status": "COMPLETED"
}
```

### Successful Response

```http
200 OK
```

The response contains the updated payment.

---

## Delete Payment

### Request

```http
DELETE /api/payments/{id}
```

### Successful Response

```http
200 OK
```

```json
{
  "id": "3b9e3e41-781f-42d7-8409-3e7f62f2f786",
  "message": "Payment deleted successfully",
  "timestamp": "2026-07-14T12:45:00Z"
}
```

---

## Validation

The API validates incoming payment requests.

Examples include:

- Transaction reference is required
- Transaction reference has a maximum length of 100 characters
- Amount must be at least `0.01`
- Currency must contain exactly three alphabetic characters
- Customer ID is required
- Description has a maximum length of 255 characters
- Payment status is required when updating status

Example validation error:

```json
{
  "timestamp": "2026-07-14T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "amount": "Amount must be greater than zero"
  }
}
```

---

## Exception Handling

The API provides centralized exception handling using `@RestControllerAdvice`.

### Payment Not Found

```http
404 Not Found
```

### Duplicate Transaction Reference

```http
409 Conflict
```

Example:

```json
{
  "timestamp": "2026-07-14T12:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Payment already exists with transaction reference: TXN-20260714-001"
}
```

### Validation Failure

```http
400 Bad Request
```

---

## PostgreSQL Database

The application uses PostgreSQL for persistent payment storage.

Default local configuration:

```text
Database: paymentdb
Username: paymentuser
Port: 5432
```

The PostgreSQL database runs inside Docker, while the Spring Boot application currently runs locally.

---

## Docker Setup

Start PostgreSQL:

```bash
docker compose up -d
```

Check container status:

```bash
docker compose ps
```

Expected status:

```text
healthy
```

Stop the container:

```bash
docker compose stop
```

Start it again:

```bash
docker compose start
```

Stop and remove the container:

```bash
docker compose down
```

The PostgreSQL data is stored in a named Docker volume:

```text
enterprise-payment-api_postgres_data
```

The database data persists when the container is stopped or recreated with:

```bash
docker compose down
```

> Do not use `docker compose down -v` unless you intentionally want to delete the database volume and its stored data.

---

## Inspect the Database

Connect to PostgreSQL inside the Docker container:

```bash
docker exec -it enterprise-payment-postgres psql -U paymentuser -d paymentdb
```

List tables:

```sql
\dt
```

Query all payments:

```sql
SELECT * FROM payments;
```

Query selected fields:

```sql
SELECT transaction_reference, amount, currency, status
FROM payments;
```

Exit PostgreSQL:

```text
\q
```

---

## Swagger / OpenAPI Documentation

Interactive API documentation is available through Swagger UI.

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

Swagger provides interactive documentation for:

- Creating payments
- Retrieving payments
- Updating payment status
- Deleting payments
- Request validation schemas
- Response models
- Payment status values

---

## Running the Application

### Prerequisites

Install:

- Java 21 or compatible JDK
- Docker Desktop
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Clone the repository

```bash
git clone <repository-url>
cd enterprise-payment-api
```

### 2. Start PostgreSQL

```bash
docker compose up -d
```

### 3. Run the application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux or macOS:

```bash
./mvnw spring-boot:run
```

The application starts on:

```text
http://localhost:8080
```

---

## Automated Testing

The project currently contains automated tests covering the service and web/controller layers.

### Service Tests

Service tests use **JUnit 5** and **Mockito** to verify business logic in isolation.

Coverage includes:

- Creating payments
- Duplicate transaction reference handling
- Retrieving a payment by ID
- Payment not found handling
- Retrieving all payments
- Updating payment status
- Updating a non-existent payment
- Deleting payments
- Deleting a non-existent payment

### Controller Tests

Controller tests use **MockMvc** to verify the HTTP API layer.

Coverage includes:

- `POST /api/payments`
- `GET /api/payments`
- `GET /api/payments/{id}`
- `PATCH /api/payments/{id}/status`
- `DELETE /api/payments/{id}`
- Validation failures
- Not-found responses
- Duplicate payment responses

### Run All Tests

On Windows:

```powershell
.\mvnw.cmd test
```

On Linux or macOS:

```bash
./mvnw test
```

Current test result:

```text
Tests run: 20
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

## Current Project Capabilities

The application currently demonstrates:

- RESTful API design
- Layered Spring Boot architecture
- DTO-based API contracts
- PostgreSQL persistence
- JPA entity mapping
- UUID identifiers
- Transaction management
- Request validation
- Global exception handling
- Duplicate transaction prevention
- Payment lifecycle status management
- Docker-based database infrastructure
- Persistent Docker volumes
- Service-layer unit testing
- Controller-layer testing with MockMvc
- OpenAPI 3 specification generation
- Interactive Swagger documentation

---

## Roadmap

Planned enhancements:

- Testcontainers-based PostgreSQL integration tests
- JWT authentication and authorization
- Kafka-based payment event publishing
- Dockerize the Spring Boot application
- Environment-based configuration and secret management
- CI/CD pipeline
- Additional integration testing
- Cloud deployment
- Observability and monitoring improvements

---

## Author

**Vinit Joshi**

Senior Java / Backend Engineer

Experience with:

- Java
- Spring Boot
- Microservices
- REST APIs
- Apache Kafka
- PostgreSQL
- Docker
- Kubernetes
- AWS
- Google Cloud Platform

---

## License

This project is licensed under the MIT License.