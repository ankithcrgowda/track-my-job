# TrackMyJob
![CI](https://github.com/ankithcrgowda/track-my-job/actions/workflows/ci.yml/badge.svg)

TrackMyJob is a RESTful Web API built with Java 21 and Spring Boot 3 for tracking job application lifecycles. The application features stateless JWT authentication, user-isolated application management, input validation, custom exception handling, PostgreSQL persistence, and containerized deployment via Docker and Docker Compose.


## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development Setup](#local-development-setup)
  - [Running with Docker Compose](#running-with-docker-compose)
- [API Reference](#api-reference)
  - [Authentication Endpoints](#authentication-endpoints)
  - [Job Application Endpoints](#job-application-endpoints)
- [Application Statuses](#application-statuses)
- [Security Implementation](#security-implementation)
- [Testing](#testing)
- [Contributing](#contributing)
- [Credits](#credits)
- [Connect](#connect)

---

## Features

- **Stateless Authentication**: JWT-based authentication flow for user registration and secure login. Passwords hashed using BCrypt.
- **User-Isolated Data Access**: Applications are strictly isolated per authenticated user identity retrieved securely from the JWT token.
- **Job Application Management**: Full CRUD capabilities for job application records (Create, Read All, Read by ID, Update, Delete).
- **Status Lifecycle Tracking**: Explicit application status states (`APPLIED`, `INTERVIEW_SCHEDULED`, `OFFER_RECEIVED`, `REJECTED`, `WITHDRAWN`).
- **Input Validation & Exception Handling**: Centralized global exception handling (`GlobalExceptionHandler`) for validated request payloads.
- **OpenAPI 3 / Swagger Integration**: Interactive REST API documentation auto-generated via Springdoc OpenAPI.
- **Multi-Stage Dockerization**: Docker build configuration utilizing eclipse-temurin Alpine images for minimal container footprint (~200MB).
- **Docker Compose Infrastructure**: One-command service orchestration with PostgreSQL health checks and persistent storage volumes.

---

## Tech Stack

| Layer / Aspect | Technology | Details |
| :--- | :--- | :--- |
| **Language** | Java 21 | Modern Java LTS runtime environment |
| **Framework** | Spring Boot 3.5.0 | Core backend framework and dependency injection |
| **Security** | Spring Security & JJWT 0.12.6 | Stateless JWT authentication & authorization |
| **Database** | PostgreSQL 16 | Relational database management system |
| **ORM / Data Access** | Spring Data JPA / Hibernate | Entity mapping and repository abstraction |
| **API Documentation** | Springdoc OpenAPI 2.8.8 | Interactive Swagger UI documentation |
| **Productivity** | Project Lombok | Annotation processing for getters, setters, builders |
| **Containerization** | Docker & Docker Compose | Application containerization and service management |
| **Build Tool** | Apache Maven | Project build management |
| **Testing** | JUnit 5 & H2 Database | Unit and integration testing with in-memory DB |

---

## Project Architecture

```
TrackMyJob/
├── .github/                   # GitHub configuration files
├── .mvn/                      # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/com/trackmyjob/
│   │   │   ├── config/        # Security and Swagger configurations
│   │   │   ├── controller/    # REST Controllers (Auth, JobApplication)
│   │   │   ├── dto/           # Data Transfer Objects (Requests & Responses)
│   │   │   ├── entity/        # JPA Entities (User, JobApplication, ApplicationStatus)
│   │   │   ├── exception/     # Custom exceptions & GlobalExceptionHandler
│   │   │   ├── repository/    # Spring Data JPA Repositories
│   │   │   ├── security/      # JWT Filter, JwtUtil, CustomUserDetailsService
│   │   │   └── service/       # Business logic interfaces & implementations
│   │   └── resources/
│   │       ├── application.properties         # Development properties
│   │       ├── application-docker.properties  # Docker profile overrides
│   │       └── application-test.properties    # Test environment properties
│   └── test/                  # Test suite
├── Dockerfile                 # Multi-stage production build script
├── docker-compose.yml         # Container configuration for PostgreSQL & App
├── mvnw / mvnw.cmd            # Maven wrapper scripts
└── pom.xml                    # Maven project configuration
```

---

## API Documentation

Interactive API documentation powered by OpenAPI 3 and Swagger UI is accessible once the application is running:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: Version 21 or later
- **Apache Maven**: Version 3.8+ (or use the included `./mvnw`)
- **Docker Desktop**: Version 20.10+ (for database or full container runtime)
- **PostgreSQL**: Version 16+ (if executing database natively without Docker)

---

### Local Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/ankithcrgowda/TrackMyJob.git
cd TrackMyJob
```

#### 2. Start PostgreSQL Container
Launch the PostgreSQL service via Docker Compose:
```bash
docker compose up postgres -d
```
Database environment configuration:
- **Database**: `trackmyjob_db`
- **User**: `trackmyjob_user`
- **Password**: `trackmyjob_pass`
- **Port**: `5432`

#### 3. Build and Run the Application
On Linux/macOS:
```bash
./mvnw clean spring-boot:run
```
On Windows:
```cmd
mvnw.cmd clean spring-boot:run
```

The application starts on `http://localhost:8080`.

---

### Running with Docker Compose

To compile and launch both the Spring Boot service and PostgreSQL database in containerized mode:

```bash
docker compose up --build -d
```

To view real-time application logs:
```bash
docker compose logs -f app
```

To stop containers:
```bash
docker compose down
```

---

## API Reference

### Authentication Endpoints

Public endpoints for user onboarding and token generation.

| Method | Endpoint | Description | Request Body | Success Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user | `RegisterRequest` | `201 Created` (`AuthResponse`) |
| `POST` | `/api/auth/login` | Authenticate credentials | `LoginRequest` | `200 OK` (`AuthResponse`) |

#### Auth Response Format (`AuthResponse`):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "email": "user@example.com",
  "name": "Alex Johnson"
}
```

---

### Job Application Endpoints

Protected endpoints requiring HTTP header: `Authorization: Bearer <JWT_TOKEN>`

| Method | Endpoint | Description | Request Body | Success Response |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/applications` | Create job application | `JobApplicationRequest` | `201 Created` |
| `GET` | `/api/applications` | Fetch all user applications | None | `200 OK` |
| `GET` | `/api/applications/{id}` | Fetch application by ID | None | `200 OK` |
| `PUT` | `/api/applications/{id}` | Update application by ID | `JobApplicationRequest` | `200 OK` |
| `DELETE` | `/api/applications/{id}` | Delete application by ID | None | `204 No Content` |

#### Sample Application Request Payload (`JobApplicationRequest`):
```json
{
  "companyName": "Acme Corp",
  "jobRole": "Software Engineer",
  "status": "APPLIED",
  "appliedDate": "2026-09-19",
  "description": "Submitted application via official careers portal."
}
```

#### Sample Application Response Payload (`JobApplicationResponse`):
```json
{
  "id": 1,
  "companyName": "Acme Corp",
  "jobRole": "Software Engineer",
  "status": "APPLIED",
  "appliedDate": "2026-09-19",
  "description": "Submitted application via official careers portal.",
  "createdDate": "2026-09-19T10:15:30",
  "lastModifiedDate": "2026-09-19T10:15:30"
}
```

---

## Application Statuses

The `status` enumeration field accepts the following valid states:

- `APPLIED`: Application submitted.
- `INTERVIEW_SCHEDULED`: Interview stage organized.
- `OFFER_RECEIVED`: Employment offer extended.
- `REJECTED`: Application non-selected.
- `WITHDRAWN`: Application retracted by candidate.

---

## Security Implementation

- **Stateless Session Policy**: Session creation policy is set to `STATELESS`. Spring Security relies solely on Bearer JWT verification per request.
- **JWT Authorization Filter**: `JwtAuthFilter` extracts and validates JWT tokens from incoming HTTP `Authorization` headers.
- **Password Protection**: User passwords are encrypted using `BCryptPasswordEncoder`.
- **Identity Isolation**: Controllers resolve user context via `@AuthenticationPrincipal UserDetails` to ensure strict data ownership.

---

## Testing

The test profile utilizes an in-memory **H2 Database** (`application-test.properties`), allowing test suites to execute without dependencies on an active PostgreSQL instance.

To run tests:
```bash
./mvnw test
```

---

## Contributing

Contributions are welcome. If you would like to contribute:

1. Fork the repository.
2. Create a new feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request detailing your changes.

<!-- ---

## Credits

- **Spring Boot Framework**: Core backend application infrastructure and dependency management.
- **Spring Security**: Robust authentication architecture and security filter chain setup.
- **PostgreSQL**: Relational database storage engine.
- **Springdoc OpenAPI**: Automated OpenAPI 3 specification and interactive Swagger UI interface.
- **Docker**: Container orchestration and execution environment. -->

---

## Connect

Developed and maintained by **Ankith C R Gowda**.

- **GitHub Profile**: [github.com/ankithcrgowda](https://github.com/ankithcrgowda)
- **Linkedin Profile**: [linkedin.com/in/ankithcr](https://www.linkedin.com/in/ankithcr)
- **Project Repository**: [github.com/ankithcrgowda/TrackMyJob](https://github.com/ankithcrgowda/TrackMyJob)

