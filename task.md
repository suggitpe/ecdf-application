# Career Development Assessment Application

## Planning

- [x] Create initial task.md
- [x] Refine task list based on `requirements.md` and `agents.md`

## 1. Foundation & Containerization (12-Factor)

- [x] Initialize Multi-Module Gradle project (Groovy DSL)
- [x] Create `gradle/libs.versions.toml` for dependency version catalog (Java 21, Spring Boot 3.4.x, Kotlin Test, Kotest, Testcontainers)
- [x] Setup `application` (Spring Boot Web/UI) and `domain` (Core Business Logic) submodules
- [x] Setup core CSS variables and design system (Vanilla CSS, premium dark-mode aesthetic, no Tailwind)
- [x] Implement Thymeleaf layout shell (Navigation, Header, Main Content area)
- [x] Configure local file storage path in `application.yml` for Evidence attachments
- [x] Create `Containerfile` / `Dockerfile` for the Spring Boot application
- [x] Externalize configuration (file storage path) using environment variables
- [x] Setup `docker-compose.yml` (for Podman Compose) to orchestrate Spring Boot App and file storage volume

## 2. Core Domain Models & State (Ports & Adapters)

- [x] Define pure Java Domain Models/DTOs (User, Grade, Pillar, Evidence, Assessment, CheckIn, PdpItem) including Dreyfus 1-5 scale constraints
- [x] Define pure Java API Interfaces/Ports for data access (`UserRepository`, `EvidenceRepository`, etc.)
- [x] Create Liquibase migrations for Users, Grades, Expectations, Evidence, Links, Attachments, Assessments, CheckIns, and PDP tables
- [x] Define JPA Entities mapping to the Liquibase-created tables
- [x] Implement JPA-backed adapters (`JpaUserRepositoryAdapter`, etc.) that implement the Repository interfaces
- [x] Write JPA Repository integration tests in **Kotlin (JUnit5 + AssertJ)** and an H2 database

## 3. Business Logic (Services - Kotlin TDD)

- [x] Write Unit Tests (Kotlin/Kotest/Mockito) for `UserService` and `GradeService` (Mocking repository ports)
- [x] Implement `UserService` and `GradeService` to pass tests
- [x] Write Unit Tests for `EvidenceService` (Handling creation, iterations, and attachments)
- [x] Implement `EvidenceService` to pass tests
- [x] Write Unit Tests for `AssessmentService` (Independent scoring logic, 3rd-party ITA assignments)
- [x] Implement `AssessmentService` to pass tests
- [x] Write Unit Tests for `CheckInService` (Point-in-time ECDF snapshots, calculating promotion readiness including ITA requirement, handling evidence aging)
- [x] Implement `CheckInService` to pass tests
- [ ] Write Unit Tests for `PdpService` (Mandatory creation for underperforming pillars, linking to learning journeys)
- [ ] Implement `PdpService` to pass tests

## 4. UI Layer (Spring MVC Controllers & Views - Kotlin WebMvcTests)

- [ ] Security & Authentication:
  - [ ] Implement basic Spring Security setup
  - [ ] Write Controller tests (Kotlin/MockMvc) and implement `LoginController` and `login.html` view
- [ ] Dashboard View (Default for all roles):
  - [ ] Write Controller tests and implement `DashboardController` logic
  - [ ] Implement `dashboard.html` template showing holistic score radar chart (using Chart.js) and recent evidence
- [ ] Manager Team/Org View:
  - [ ] Write Controller tests and implement `TeamController` logic
  - [ ] Implement `team.html` template viewing full reporting hierarchy, grades, and latest Check-In outcomes
  - [ ] Add highlighting for staff with skill gaps and filters to identify "Ready for Promotion" employees
- [ ] Evidence Management:
  - [ ] Write Controller tests (including multipart file uploads) and implement `EvidenceController`
  - [ ] Implement `evidence-list.html`
  - [ ] Implement `evidence-form.html` (Engineers select applicable pillars and self-rate; provide Impact, Complexity, Contribution)
  - [ ] Implement secure file download endpoint for attachments
- [ ] Evidence Assessment & 3rd-Party Workflow:
  - [ ] Write Controller tests and implement `AssessmentController`
  - [ ] Implement `assessment-form.html` (for Managers/ITAs) with independent pillar scoring and Review Summary
  - [ ] Implement UI flow for Manager to assign evidence to an Independent Technical Assessor (ITA)
  - [ ] Implement `assessor-queue.html` listing evidence assigned to the current user
- [ ] Check-In & Period Review (Quarterly):
  - [ ] Write Controller tests and implement `CheckInController`
  - [ ] Implement `checkin-form.html` for Managers to capture review notes and freeze the point-in-time aggregated scores
- [ ] Developer Pathways & PDP:
  - [ ] Write Controller tests and implement `FrameworkController` mapping roles to expected pillar scores
  - [ ] Write Controller tests and implement `PdpController`
  - [ ] Implement `pdp.html` connecting identified gaps to learning journeys

## 5. Polish & Verification

- [ ] Refine visual design (glassmorphism details, animations, responsive layout)
- [ ] Perform manual end-to-end verification
- [ ] Generate `walkthrough.md` documenting implemented flows
