# Engineer Career Development Framework (ECDF)

The Engineer Career Development Framework (ECDF) application is an internal tool designed to manage software engineer career progression. It provides standardized progression tracking through assessments, evidence submissions, and personal development plans based on an 8-pillar framework.

## Core Features

- **Standardized Progression**: Objective criteria for career advancement using the 8-pillar ECDF framework.
- **Evidence Tracking**: Capture evidence submissions as immutable records detailing impact, complexity, and personal contribution.
- **Premium UX**: A sleek dark mode interface with glassmorphism elements and modern typography.

## Getting Started

### Prerequisites

- JDK 21 (Azul Zulu JRE 21 recommended)
- Docker & Docker Compose (for infrastructure and integration tests)
- Gradle

### Building the Project

The application is a multi-module Gradle project (`domain` and `application` modules).

```bash
# Build the project and run all tests
./gradlew build

# Run JaCoCo test coverage
./gradlew jacocoTestReport
```

### Running the Application

To run the application locally:

```bash
./gradlew :application:bootRun
```

Or using Docker Compose:

```bash
docker-compose up -d
```

## Development Guidelines

- **Strict TDD**: All tests must be written in **Kotlin** using JUnit 5 and Mockito/Kotest before production code.
- **Architecture**: The `domain` module contains pure Java business logic and interfaces. Framework-specific logic (e.g., Spring annotations, JPA) must only exist in the `application` module.
- **Frontend**: The styling uses Vanilla CSS (no Tailwind) adhering to the Premium Dark Mode aesthetic.
