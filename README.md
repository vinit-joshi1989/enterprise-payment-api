# Enterprise Payment API

A production-oriented backend application built using Java 21 and Spring Boot 3.

This project is being developed to demonstrate enterprise backend development practices including secure REST APIs, clean architecture, testing, containerization, event-driven architecture, and CI/CD.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL *(coming soon)*
- Docker & Docker Compose *(coming soon)*
- Apache Kafka *(coming soon)*
- JUnit 5 & Mockito *(coming soon)*
- GitHub Actions *(coming soon)*

## Current Features

- ✅ Health Check API
- ✅ Spring Security Configuration
- ✅ Layered Project Structure
- ✅ Maven Build

## Health Endpoint

```
GET /api/health
```

Example Response

```json
{
  "timestamp": "2026-07-09T10:31:02.903278900Z",
  "status": "UP",
  "version": "1.0.0",
  "application": "Enterprise Payment API"
}
```

## Planned Features

- Payment Management APIs
- JWT Authentication & Authorization
- PostgreSQL Integration
- Docker & Docker Compose
- Apache Kafka Event Publishing
- Global Exception Handling
- Validation
- Swagger / OpenAPI
- Unit & Integration Testing
- GitHub Actions CI/CD

## Running the Project

```bash
./mvnw spring-boot:run
```

Application URL:

```
http://localhost:8080
```

Health Check:

```
http://localhost:8080/api/health
```

## Project Status

🚧 Currently under active development.

This repository is being developed incrementally using small, production-style commits.