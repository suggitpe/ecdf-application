# Technology Stack

## Backend
- **Language**: Java 21 (with Kotlin strictly for unit testing)
- **Framework**: Spring Boot 3.5.x (Web, Data JPA, Security, Validation)
- **Architecture**: Hexagonal (Ports & Adapters) Architecture

## Database
- **Datastore**: PostgreSQL (Production) / H2 in-memory (Development/Testing)
- **Migrations**: Schema managed via Liquibase

## Frontend
- **Rendering**: Thymeleaf (Server-Side Rendering)
- **Styling**: Vanilla CSS (Premium Dark Mode Aesthetic)

## Build & Testing
- **Build Tool**: Gradle (Groovy DSL)
- **Testing Frameworks**: JUnit 5, Kotest Assertions, Mockito, Testcontainers, JaCoCo