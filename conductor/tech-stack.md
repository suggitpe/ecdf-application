# Technology Stack

## Core Technologies
- **Backend Language**: Java 24 (with Kotlin for testing)
- **Backend Framework**: Spring Boot 4 (Latest)
- **Build System**: Multi-module Gradle (Groovy DSL)
- **Architecture**: Ports & Adapters (Hexagonal)

## Frontend & UI
- **Template Engine**: Thymeleaf (Server-Side Rendering)
- **Styling**: Vanilla CSS (Premium Dark Mode)

## Data Storage & Persistence
- **Database**: H2 (In-memory) for development, PostgreSQL for testing
- **Schema Management**: Liquibase
- **Persistence Framework**: Spring Data JPA

## Testing & Quality Assurance
- **Test Runner**: JUnit 5
- **Assertions**: Kotest
- **Mocking**: Mockito
- **Environment**: Testcontainers (PostgreSQL)
- **Coverage**: JaCoCo (>80% required)

## Infrastructure & DevOps
- **Containerization**: Docker / Podman
- **Infrastructure as Code**: Terraform
- **Deployment**: Google Cloud Run (via GitHub Actions)
