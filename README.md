# BookMyShow – Event-Driven Movie Booking System

A movie ticket booking backend built using **Java, Spring Boot, Apache Kafka, and MySQL**. This project demonstrates microservices communication and the **Saga Orchestration pattern** to coordinate booking, seat reservation, and payment workflows.

## Architecture

The application consists of five modules:

- **Booking Orchestrator:** Coordinates the booking workflow and manages the Saga.
- **Booking Service:** Creates and manages booking records.
- **Seat Inventory Service:** Checks seat availability, reserves seats, and handles seat release during compensation.
- **Payment Service:** Simulates payment processing and publishes payment results.
- **Movie Booking Commons:** Contains shared DTOs, event models, and Kafka-related constants.

### Booking Workflow

1. The client sends a booking request to the Booking Orchestrator.
2. The orchestrator initiates the booking workflow using Kafka events.
3. The Booking Service creates the booking record.
4. The Seat Inventory Service checks and reserves the requested seats.
5. The Payment Service processes the simulated payment.
6. The orchestrator coordinates the final outcome.
7. If a step fails, compensating actions can be triggered to roll back previously completed operations.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA and Hibernate
- Apache Kafka
- MySQL
- Maven
- Docker Compose
- Springdoc OpenAPI / Swagger UI

## Project Structure

```text
BookMyShow/
├── booking-orchestrator/
├── booking-service/
├── movie-booking-commons/
├── payment-service/
├── seat-inventory-service/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Prerequisites

Install the following before running the application:

- JDK 21
- Docker Desktop and Docker Compose
- MySQL
- Git

Verify Java installation:

```bash
java -version
```

## Configuration

The Booking Service and Seat Inventory Service require the `DB_PASSWORD` environment variable.

### Windows PowerShell

```powershell
$env:DB_PASSWORD = "your_local_mysql_password"
```

Configure the same variable in your IDE run configuration if you start the applications through IntelliJ IDEA.

The database configuration currently uses `localhost:3306/BooKMyShow`. Ensure the database exists and that the configured MySQL user has the required permissions.

**Important:** Never commit actual database passwords, API keys, or other secrets to GitHub.

## Running the Project

### 1. Start Kafka

From the project root, run:

```bash
docker compose up -d
```

Check the running containers:

```bash
docker compose ps
```

The supplied Docker Compose configuration exposes Kafka on port `9092`.

### 2. Install the Shared Commons Module

The microservices depend on the `movie-booking-commons` module.

On Windows:

```powershell
cd movie-booking-commons
.\mvnw.cmd clean install
cd ..
```

On Linux/macOS:

```bash
cd movie-booking-commons
./mvnw clean install
cd ..
```

### 3. Build the Microservices

Build each service separately.

Windows PowerShell:

```powershell
cd booking-service
.\mvnw.cmd clean package
cd ..

cd seat-inventory-service
.\mvnw.cmd clean package
cd ..

cd payment-service
.\mvnw.cmd clean package
cd ..

cd booking-orchestrator
.\mvnw.cmd clean package
cd ..
```

On Linux/macOS, use `./mvnw` instead of `.\mvnw.cmd`.

### 4. Start the Microservices

Start each service in a separate terminal after Kafka and MySQL are running.

From each service directory, execute:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell, use:

```powershell
.\mvnw.cmd spring-boot:run
```

Start all four services:

- Booking Service
- Seat Inventory Service
- Payment Service
- Booking Orchestrator

## Booking API

The Booking Orchestrator exposes the following endpoint:

```http
POST http://localhost:8084/orchestrator/bookings
Content-Type: application/json
```

Example request:

```json
{
  "userId": "USER_101",
  "showId": "SHOW_101",
  "seatIds": ["A1", "A2"],
  "amount": 1200
}
```

Adjust the JSON fields according to the `BookingRequest` model in the shared commons module.

The workflow uses asynchronous Kafka communication. Receiving a booking ID does not necessarily mean that the booking has been confirmed.

## Swagger API Documentation

When the Booking Orchestrator is running, access:

- Swagger UI: http://localhost:8084/swagger-ui.html
- OpenAPI JSON: http://localhost:8084/v3/api-docs

## Key Concepts Demonstrated

- Microservices architecture
- Event-driven communication with Apache Kafka
- Saga Orchestration pattern
- Distributed transaction coordination
- Compensating transactions
- Database persistence using Spring Data JPA
- REST API development
- Docker-based infrastructure

## Future Improvements

- Add idempotent Kafka consumers and retry mechanisms.
- Implement dead-letter topics for failed messages.
- Improve seat reservation concurrency and transactional consistency.
- Add comprehensive unit and integration tests.
- Add health checks and distributed tracing.
- Containerize all microservices and databases.
- Add authentication and authorization.
- Improve configuration management using environment variables.

## Disclaimer

This project is intended for learning and demonstration purposes. Payment processing is simulated and the application is not intended for production use without additional security, reliability, and testing.

## License

No license has been specified. Add a suitable `LICENSE` file if you intend to distribute this project as open-source software.
