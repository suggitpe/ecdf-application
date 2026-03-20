# Specification: Spring Boot 3.5.12 Upgrade

## 1. Overview
The goal of this chore is to upgrade the ECDF application's Spring Boot dependency to version 3.5.12. This ensures the application stays up to date with the latest features, security patches, and bug fixes provided by the Spring ecosystem.

## 2. Scope
*   **Dependency Updates:** Upgrade Spring Boot to version 3.5.12.
*   **Comprehensive Update:** Update all other outdated dependencies in the Gradle Version Catalog (`gradle/libs.versions.toml`) to their latest compatible versions (e.g., Testcontainers, Mockito, Kotlin).
*   **Gradle Build:** Ensure the project continues to compile and all tests pass with the new dependencies.
*   **Address Breaking Changes:** Address any immediate breaking changes introduced in Spring Boot 3.5 (e.g., `TaskExecutor` bean names, Profile naming validation) to ensure the application starts and runs correctly.

## 3. Out of Scope
*   **Fixing Deprecations:** Any code elements deprecated in this upgrade will **not** be refactored during this track. Follow-up tasks (tracks) will be created to address them later.
*   **Adding New Features:** No new application features will be implemented.
*   **Major Architecture Changes:** The Ports & Adapters architecture remains unchanged.

## 4. Acceptance Criteria
*   The `gradle/libs.versions.toml` file reflects Spring Boot version `3.5.12`.
*   All other dependencies in `gradle/libs.versions.toml` are updated to their latest stable and compatible versions.
*   The application compiles successfully using `./gradlew build`.
*   The entire test suite (`./gradlew test`) passes successfully with JaCoCo coverage maintained at >80%.
*   The application starts up successfully without fatal errors from Spring Boot 3.5.12 breaking changes.
*   Follow-up tracks/issues are logged for any identified deprecations.