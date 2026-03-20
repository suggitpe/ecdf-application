# Implementation Plan: Spring Boot 3.5.12 Upgrade

## Phase 1: Update Dependencies [checkpoint: 5c511e5]
- [x] Task: Update Spring Boot version to `3.5.12` in `gradle/libs.versions.toml` 259d79b
- [x] Task: Review and update all other dependencies (Kotlin, Mockito, Testcontainers, Spring Cloud, etc.) to their latest compatible versions in `gradle/libs.versions.toml` 5dc331d
- [x] Task: Reload Gradle configuration and run `./gradlew clean build -x test` to verify the project compiles with new dependencies a9f4eef
- [x] Task: Conductor - User Manual Verification 'Update Dependencies' (Protocol in workflow.md) 5c511e5

## Phase 2: Resolve Breaking Changes & Verification
- [x] Task: Run `./gradlew test` to identify any failing tests due to breaking changes 6de3ef1
- [x] Task: Address any immediate Spring Boot 3.5 breaking changes (e.g., `TaskExecutor` bean names, strict profile validation) to restore test suite health 377c326
- [x] Task: Run the application locally and verify successful startup 4ff6b7d
- [x] Task: Identify deprecated usages in the codebase resulting from the upgrade and log them as new Conductor tracks/tasks for future work c41e6b9
- [x] Task: Ensure JaCoCo coverage remains above the 80% threshold (`./gradlew jacocoTestReport`) 77dc58b
- [~] Task: Conductor - User Manual Verification 'Resolve Breaking Changes & Verification' (Protocol in workflow.md)