# LLM Steering & Technical Directives

This document provides core steering instructions for AI Assistants working on the ECDF Application.

## 1. Execution Workflow (CRITICAL)

- **Task Boundaries & Human Approval**:
  - For **each** task in the checklist, you MUST explicitly ask for a human to strongly agree to start the next task.
  - Once a task has completed, you MUST ask a human to review what has been created before moving on.
- **Do NOT run all steps in one go**. Execute one logical block, verify it compiles and runs, then pause for human review via standard chat or `notify_user`.
- **Git Commits**: Committing any changes must be a human-in-the-loop activity. Do NOT run `git commit` commands automatically without explicit user approval.
- Before making significant architectural decisions or writing massive amounts of code, confirm the approach with the user.

## 2. Technical Stack & Architecture

- **Backend Language**: Java 21
- **Build Tool**: Gradle (using Groovy DSL).
- **Project Structure**: Must be a **multi-module** Gradle project.
- **Dependency Management**: Dependencies must be segregated from their versions. Strict requirement to use a Gradle Version Catalog (`gradle/libs.versions.toml`).
- **Versioning**: The build must use date-based versioning (`yyyy.MM.dd.HHmmss`) rather than standard semantic versioning (e.g. not 0.0.1).
- **Framework**: Spring Boot 3.4.x (Web, Data JPA, Security, Validation, Thymeleaf)
- **Frontend**: Thymeleaf templates (Server-Side Rendering).
- **Styling**: Vanilla CSS. Must use a Premium Dark Mode aesthetic with glassmorphism elements and modern typography. No Tailwind.
- **Database**: H2 in-memory database (schema managed via Liquibase).
  - *Critical*: Always initialize an empty master changelog file at `src/main/resources/db/changelog/db.changelog-master.yaml` to prevent Liquibase from failing application startup.
- **Testing**: Strict Test-Driven Development (TDD). JUnit 5, Mockito, `@WebMvcTest`, and **Testcontainers** for DB integration tests. **JaCoCo** is used for test coverage metrics and is included in the default Gradle task (`.\gradlew`).
- **Environment**: 12-Factor App design. Configuration externalized via environment variables. Containerized via standard `Dockerfile`/`Containerfile` and `docker-compose.yml`.

## 3. Workday Integration Strategy (Ports & Adapters)

- **Goal**: The long-term system of record is **Workday**. The database will eventually be swapped out.
- **Architecture**: The application MUST follow the **Repository Pattern (Hexagonal / Ports & Adapters architecture)**.
- **Rule 1**: The Business layer (`@Service`) must only interact with pure Java interface "Ports" (`UserRepository`, `EvidenceRepository`) and pure Java Domain Records/DTOs.
- **Rule 2**: No DB-specific logic (JPA Annotations, SQL) can leak into the Domain or Service layers.
- **Current Phase**: Implement a `persistence` adapter using Spring Data JPA (`UserEntity`, `JpaUserRepositoryAdapter`, etc.) that translates between JPA entities and Domain records.

## 4. Test-Driven Development (TDD) Mandate (CRITICAL)

- **Test Language & Framework**: All Unit Tests MUST be written in **Kotlin**. Production code remains in Java.
- **Assertions**: You MUST use **Kotest assertions**.
- **Test Framework**: Use **JUnit 5** as the test runner and **Mockito** for mocking.
- **Behavioral Testing ONLY**: Testing MUST focus strictly on the *behavior* of the classes. Do NOT write property-based tests or getter/setter tests for simple POJOs/Records.
- **Test-First Approach**: You MUST write tests BEFORE implementing the production code. The Red-Green-Refactor cycle is non-negotiable.
- **Test Execution Flow**: Before starting any new task, you MUST run all tests to ensure a clean slate. Once a task is completed, you MUST run all tests again and present the results (including JaCoCo Test Coverage) to the user BEFORE asking them to commit the code.
- **Repository Testing**: Write `@DataJpaTest` integration tests using **Testcontainers** before creating JPA entities and repository adapters.
- **Service Testing**: Write pure unit tests using JUnit 5 and Mockito (`@ExtendWith(MockitoExtension.class)`) to isolate business logic before implementing `@Service` classes.
- **Web/Controller Testing**: Write `@WebMvcTest` tests to verify controller routing, mapping, validation, and view selection before implementing the Spring MVC `@Controller`s.
- **Minimal Code**: Do not write production code unless it is specifically to make a failing test pass. Always ensure tests fail for the right reason before fixing them.
