# Career Development Assessment Application

## Planning
- [x] Create initial task.md
- [x] Refine task list based on `requirements.md` and `agents.md`

## 1. Foundation & Containerization (12-Factor)
- [x] Initialize Multi-Module Gradle project (Groovy DSL)
- [ ] Create `gradle/libs.versions.toml` for dependency version catalog (Java 21, Spring Boot 3.4.x, Kotlin Test, Kotest, Testcontainers)
- [ ] Setup `application` (Spring Boot Web/UI) and `domain` (Core Business Logic) submodules
- [ ] Setup core CSS variables and design system (Vanilla CSS, premium dark-mode aesthetic, no Tailwind)
- [ ] Implement Thymeleaf layout shell (Navigation, Header, Main Content area)
- [ ] Configure local file storage path in `application.yml` for Evidence attachments
- [ ] Create `Containerfile` / `Dockerfile` for the Spring Boot application
- [ ] Externalize configuration (DB credentials, file storage path) using environment variables
- [ ] Setup `docker-compose.yml` (for Podman Compose) to orchestrate Postgres, Spring Boot App, and file storage volume

## 2. Core Domain Models & State (Ports & Adapters)
- [ ] Define pure Java Domain Models/DTOs (User, Grade, Pillar, Evidence, Assessment, CheckIn, PdpItem) including Dreyfus 1-5 scale constraints
- [ ] Define pure Java API Interfaces/Ports for data access (`UserRepository`, `EvidenceRepository`, etc.)
- [ ] Create Liquibase migrations for Users, Grades, Expectations, Evidence, Links, Attachments, Assessments, CheckIns, and PDP tables
- [ ] Define JPA Entities mapping to the Liquibase-created tables
- [ ] Implement JPA-backed adapters (`JpaUserRepositoryAdapter`, etc.) that implement the Repository interfaces
- [ ] Write JPA Repository integration tests in **Kotlin using Kotest** and Testcontainers (PostgreSQL)

## 3. Business Logic (Services - Kotlin TDD)
- [ ] Write Unit Tests (Kotlin/Kotest/Mockito) for `UserService` and `GradeService` (Mocking repository ports)
- [ ] Implement `UserService` and `GradeService` to pass tests
- [ ] Write Unit Tests for `EvidenceService` (Handling creation, iterations, and attachments)
- [ ] Implement `EvidenceService` to pass tests
- [ ] Write Unit Tests for `AssessmentService` (Independent scoring logic, 3rd-party ITA assignments)
- [ ] Implement `AssessmentService` to pass tests
- [ ] Write Unit Tests for `CheckInService` (Point-in-time ECDF snapshots, calculating promotion readiness including ITA requirement, handling evidence aging)
- [ ] Implement `CheckInService` to pass tests
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
