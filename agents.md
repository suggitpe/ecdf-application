# LLM Steering & Technical Directives

This document provides core steering instructions for AI Assistants working on the ECDF Application.

## 1. Execution Workflow (CRITICAL)

- **Task Boundaries & Human Approval**:
  - For **each** task in the checklist, you MUST explicitly ask for a human to strongly agree to start the next task.
  - Once a task has completed, you MUST ask a human to review what has been created before moving on.
- **Do NOT run all steps in one go**. Execute one logical block, verify it compiles and runs, then pause for human review via standard chat or `notify_user`.
- **Git Commits**: Committing any changes must be a human-in-the-loop activity. Do NOT run `git commit` commands automatically without explicit user approval.
- **CI/CD Pipeline**: Any code changes committed and pushed to the repository should trigger a GitHub Actions build pipeline. Ensure that GitHub Actions is properly configured (e.g., in `.github/workflows/build.yml`) to automatically build the application, execute all tests, and upload JaCoCo coverage reports on every push and pull request.
- Before making significant architectural decisions or writing massive amounts of code, confirm the approach with the user.

## 2. Technical Stack & Architecture

- **Backend Language**: Java 21
- **Build Tool**: Gradle (using Groovy DSL).
- **Project Structure**: Must be a **multi-module** Gradle project.
- **Dependency Management**: Dependencies must be segregated from their versions. Strict requirement to use a Gradle Version Catalog (`gradle/libs.versions.toml`).
  - *Critical*: Always ensure test dependencies (e.g., Mockito, Kotest, Testcontainers) are explicitly declared in the specific module's `build.gradle` file where the tests reside (e.g., `domain/build.gradle` vs `application/build.gradle`) before writing tests.
- **Versioning**: The build must use date-based versioning (`yyyy.MM.dd.HHmmss`) rather than standard semantic versioning (e.g. not 0.0.1).
- **Framework**: Spring Boot 3.4.x (Web, Data JPA, Security, Validation, Thymeleaf)
- **Frontend**: Thymeleaf templates (Server-Side Rendering).
- **Styling**: Vanilla CSS. Must use a Premium Dark Mode aesthetic with glassmorphism elements and modern typography. No Tailwind.
- **Frontend Guidelines**: 
  - When rendering form `<select>` elements for scoring (e.g., 1-5 Dreyfus scale), the default selection for both employees and assessors MUST be a blank disabled `<option>` placeholder, never a predetermined integer.
  - The employee dashboard must always render a list of the employee's historical check-in records.
- **Domain Constraints**: The ECDF framework strictly defines **8 pillars**. You must never introduce a 9th pillar (e.g., do not include the deprecated "DEFINES" pillar).
- **Database**: H2 in-memory database (schema managed via Liquibase).
  - *Critical*: Always initialize an empty master changelog file at `src/main/resources/db/changelog/db.changelog-master.yaml` to prevent Liquibase from failing application startup.
- **Testing**: Strict Test-Driven Development (TDD). JUnit 5, Mockito, `@WebMvcTest`, and **Testcontainers** for DB integration tests. **JaCoCo** is used for test coverage metrics and is included in the default Gradle task (`.\gradlew`).
- **Environment**: 12-Factor App design. Configuration externalized via environment variables. Containerized using Azul Zulu JRE 21 via standard `Dockerfile`/`Containerfile` and `docker-compose.yml`. *Note*: The `Dockerfile` should act as a runtime wrapper; the application `.jar` must be built on the host machine before building the container image.

## 3. Workday Integration Strategy (Ports & Adapters)

- **Goal**: The long-term system of record is **Workday**. The database will eventually be swapped out.
- **Architecture**: The application MUST follow the **Repository Pattern (Hexagonal / Ports & Adapters architecture)**.
- **Rule 1**: The Business layer (`@Service`) must only interact with pure Java interface "Ports" (`UserRepository`, `EvidenceRepository`) and pure Java Domain Records/DTOs.
- **Rule 2**: No DB-specific logic (JPA Annotations, SQL) **OR Framework-specific logic (e.g., Spring `@Service`, `@Component`, `@Autowired`)** can leak into the Domain layer. The domain `Service` classes must be instantiated as simple Java beans via a Spring `@Configuration` class located in the `application` (infrastructure) module.
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
- **Teardown & Cleanup**: All tests MUST clean up after themselves. If a test creates local files (e.g., file uploads), use JUnit 5's `@TempDir` to ensure automatic deletion. Do NOT pollute the project's `data/` or `storage/` directories with test artifacts.

