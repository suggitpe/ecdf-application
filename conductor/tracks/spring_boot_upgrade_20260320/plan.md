# Implementation Plan: Spring Boot 3.5.12 Upgrade

## Phase 1: Update Dependencies
- [x] Task: Update Spring Boot version to `3.5.12` in `gradle/libs.versions.toml` 259d79b
- [x] Task: Review and update all other dependencies (Kotlin, Mockito, Testcontainers, Spring Cloud, etc.) to their latest compatible versions in `gradle/libs.versions.toml` 5dc331d
- [x] Task: Reload Gradle configuration and run `./gradlew clean build -x test` to verify the project compiles with new dependencies a9f4eef
- [~] Task: Conductor - User Manual Verification 'Update Dependencies' (Protocol in workflow.md)

## Phase 2: Resolve Breaking Changes & Verification
- [ ] Task: Run `./gradlew test` to identify any failing tests due to breaking changes
- [ ] Task: Address any immediate Spring Boot 3.5 breaking changes (e.g., `TaskExecutor` bean names, strict profile validation) to restore test suite health
- [ ] Task: Run the application locally and verify successful startup
- [ ] Task: Identify deprecated usages in the codebase resulting from the upgrade and log them as new Conductor tracks/tasks for future work
- [ ] Task: Ensure JaCoCo coverage remains above the 80% threshold (`./gradlew jacocoTestReport`)
- [ ] Task: Conductor - User Manual Verification 'Resolve Breaking Changes & Verification' (Protocol in workflow.md)