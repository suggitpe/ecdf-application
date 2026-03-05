# Career Development Assessment Application

## Planning
- [x] Create initial task.md
- [ ] Refine task list based on `requirements.md` and `agents.md`

## 1. Foundation & Containerization (12-Factor)
- [ ] Initialize Multi-Module Gradle project (Groovy DSL)
- [ ] Create `gradle/libs.versions.toml` for dependency version catalog
- [ ] Setup `application` (Spring Boot Web/UI) and `domain` or `core` submodules as needed
- [ ] Setup core CSS variables and design system (Vanilla CSS, premium dark-mode aesthetic, no Tailwind)
- [ ] Implement Thymeleaf layout shell (Navigation, Header, Main Content area)
- [ ] Configure local file storage path in `application.yml` for Evidence attachments
- [ ] Create `Containerfile` / `Dockerfile` for the Spring Boot application
- [ ] Externalize configuration (DB credentials, file storage path) using environment variables
- [ ] Setup `docker-compose.yml` (for Podman Compose) to orchestrate Postgres, Spring Boot App, and file storage volume

## 2. Core Domain Models & State (Ports & Adapters)
- [ ] Define pure Java Domain Models/DTOs (User, Grade, Pillar, Evidence, Assessment, CheckIn, PdpItem)
- [ ] Define pure Java API Interfaces/Ports for data access (`UserRepository`, `EvidenceRepository`, etc.)
- [ ] Create Liquibase migrations for Users, Grades, Expectations, Evidence, Links, Attachments, Assessments, CheckIns, and PDP tables
- [ ] Define JPA Entities mapping to the Liquibase-created tables
- [ ] Implement JPA-backed adapters (`JpaUserRepositoryAdapter`, etc.) that implement the Repository interfaces
- [ ] Write JPA Repository integration tests using `@DataJpaTest` and Testcontainers (PostgreSQL)

## 3. Business Logic (Services - TDD)
- [ ] Write Unit Tests for `UserService` and `GradeService` (Mocking repository ports)
- [ ] Implement `UserService` and `GradeService` to pass tests
- [ ] Write Unit Tests for `EvidenceService` (Handling creation, updates, and File I/O for attachments)
- [ ] Implement `EvidenceService` to pass tests
- [ ] Write Unit Tests for `AssessmentService` (Scoring logic, 3rd-party assignments)
- [ ] Implement `AssessmentService` to pass tests
- [ ] Write Unit Tests for `CheckInService` (Aggregating holistic scores, calculating promotion readiness, handling evidence aging)
- [ ] Implement `CheckInService` to pass tests
- [ ] Write Unit Tests for `PdpService`
- [ ] Implement `PdpService` to pass tests

## 4. UI Layer (Spring MVC Controllers & Views - TDD)
- [ ] Security & Authentication:
  - [ ] Implement basic Spring Security setup (in-memory or DB-backed users for demo)
  - [ ] Implement `LoginController` and `login.html` view
- [ ] Dashboard View (Default for all roles): 
  - [ ] Implement `DashboardController` tests and logic
  - [ ] Implement `dashboard.html` template showing holistic score radar chart (using Chart.js via CDN) and recent evidence
- [ ] Manager Team/Org View:
  - [ ] Implement `TeamController` tests and logic
  - [ ] Implement `team.html` template listing direct reports, grades, and latest Check-In outcomes
  - [ ] Add filtering for "Ready for Promotion" employees
- [ ] Evidence Management:
  - [ ] Implement `EvidenceController` tests (including multipart file uploads) and logic
  - [ ] Implement `evidence-list.html`
  - [ ] Implement `evidence-form.html` (for Engineers) with URL and file upload support
  - [ ] Implement secure file download endpoint for attachments
- [ ] Evidence Assessment & 3rd-Party Workflow:
  - [ ] Implement `AssessmentController` tests and logic
  - [ ] Implement `assessment-form.html` (for Managers/3rd-Parties) with pillar scoring and Review Summary
  - [ ] Implement UI flow for Manager to assign evidence to a 3rd-Party Assessor
  - [ ] Implement `assessor-queue.html` listing evidence assigned to the current user
- [ ] Check-In & Period Review (Quarterly):
  - [ ] Implement `CheckInController` tests and logic
  - [ ] Implement `checkin-form.html` for Managers to capture review notes and view aggregated scores
- [ ] Developer Pathways & PDP: 
  - [ ] Implement `FrameworkController` mapping roles to expected pillar scores
  - [ ] Implement `PdpController` tests and logic
  - [ ] Implement `pdp.html` for gap analysis and action items

## 5. Polish & Verification
- [ ] Refine visual design (glassmorphism details, animations, responsive layout)
- [ ] Perform manual end-to-end verification
- [ ] Generate `walkthrough.md` documenting implemented flows
