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
- [x] Write Unit Tests for `PdpService` (Mandatory creation for underperforming pillars, linking to learning journeys)
- [x] Implement `PdpService` to pass tests
- [x] Implement domain logic for Grade target expectations and enhanced CheckInStatus resolution (>3 pillars threshold)

## 4. UI Layer (Spring MVC Controllers & Views - Kotlin WebMvcTests)

- [x] Security & Authentication:
  - [x] Implement basic Spring Security setup
  - [x] Write Controller tests (Kotlin/MockMvc) and implement `LoginController` and `login.html` view
- [x] Dashboard View (Default for all roles):
  - [x] Write Controller tests and implement `DashboardController` logic
  - [x] Implement `dashboard.html` template showing holistic score radar chart (using Chart.js) and recent evidence
  - [x] Display a full historical log of all past Check-Ins on the standard Dashboard (Sorted most recent first).
- [x] Manager Team/Org View:
  - [x] Write Controller tests and implement `TeamController` logic
  - [x] Implement `team.html` template viewing full reporting hierarchy, grades, and latest Check-In outcomes
  - [x] Add highlighting for staff with skill gaps and filters to identify "Ready for Promotion" employees
- [x] Evidence Management:
  - [x] Write Controller tests (including multipart file uploads) and implement `EvidenceController`
  - [x] Implement `evidence-list.html` (Sorted most recent first)
  - When completing a piece of evidence and the user selects a pillar rating, they MUST also add a description (rationale) of why they have selected that rating.
  - **Evidence Traceability**: When rendering pillar scores in a Check-In record or the Employee Dashboard, the UI must include a direct hyperlink to the source evidence used to calculate that specific score.
  - [x] Implement `evidence-form.html` (Engineers select applicable pillars and self-rate; provide Impact, Complexity, Contribution)
  - [x] Mandatory Rationale: Require a description for each pillar rating provided.
  - [x] Dynamically default employee rating dropdowns to a blank placeholder
  - [x] Implement secure file download endpoint for attachments
- [x] Evidence Assessment & 3rd-Party Workflow:
  - [x] Write Controller tests and implement `AssessmentController`
  - [x] Implement `assessment-form.html` (for Managers/ITAs) with independent pillar scoring and Review Summary
  - [x] **Assessor Defaults**: Dynamically default assessor rating dropdowns to a blank placeholder (Removed employee-score defaulting to enforce independent assessment).
  - [x] **Assessor Rationale**: Require a mandatory rationale description from the assessor for each pillar rating provided.
  - [x] View Employee Evidence summary (Manager/ITA view)
  - [x] Add Evidence button to direct reports on the Team page
  - [x] Implement UI flow for Manager to assign evidence to an Independent Technical Assessor (ITA)
  - [x] **Sequential Workflow**: Enforce Manager assessment before ITA assignment/assessment logic in `AssessmentService`. Once an ITA is assigned, status transitions to `UNDER_INDEPENDENT_REVIEW`. Once an ITA assessment is finalized, the status transitions to `INDEPENDENTLY_ASSESSED`.
  - [x] Implement `assessor-queue.html` listing evidence assigned to the current user
- [x] Check-In & Period Review:
  - [x] Write Controller tests and implement `CheckInController`
  - [x] Implement `checkin-list.html` (Sorted most recent first)
  - [x] Implement `checkin-detail.html` (Read-only view for finalized/draft check-ins)
  - [x] Implement `checkin-edit.html` (Editing support for DRAFT check-ins)
  - [x] Implement `checkin-form.html` for Managers to capture review notes and support Save-as-Draft or Finalize
  - [x] Refined `CheckIn` to a point-in-time assessment model (Removed period dates)
  - [x] Updated pillar calculation to use the **most recent** assessment for each pillar
  - [x] Enhanced UI to display **N/A** for unrated pillars across dashboard, check-in details, and forms
  - [x] Surface Grade expectations, full pillar names, and actual scores visually within the Check-In UI
  - [x] Link pillar scores in Check-In and Dashboard UI to source evidence for full traceability
  - [x] Update persistence layer (Liquibase and JPA) to store evidence IDs with pillar scores

- [x] Infrastructure & CI/CD:
  - [x] Provision GCP Infrastructure using Terraform (Cloud Run, Artifact Registry, GCS Bucket)
  - [x] Configure dedicated Terraform `.tfvars` with targeted GCP project data.
  - [x] Configure GCS FUSE mount for persistent evidence storage
  - [x] Implement GitHub Actions workflow for automated GCP deployment
  - [x] Remove redundant Jib plugin and establish standard Dockerfile/gcloudignore deployment pipeline.
  - [ ] Upgrade GitHub Actions GCP Authentication from static JSON keys to Workload Identity Federation (WIF) (Recommended for enhanced CI/CD security).
- [x] Developer Pathways & PDP:
  - [x] Write Controller tests and implement `FrameworkController` mapping roles to expected pillar scores
  - [x] Write Controller tests and implement `PdpController`
  - [x] Implement `pdp.html` connecting identified gaps to learning journeys
  - [x] Implement manual PDP creation endpoint and UI on Check-In detail page
- [x] Administrator Role & Framework Management:
  - [x] Database Schema & Entities: Create Liquibase migrations, JPA Entities, and pure Java Domain models/ports for dynamic Framework Management (Pillars, Descriptions, Examples, Roles, Grades expectations)
  - [x] Business Logic: Update `FrameworkService` (or equivalent) to transition from static definitions to database-backed data, allowing for updates
  - [ ] Write Controller tests and implement `AdminController` for handling framework configuration forms
  - [ ] Implement Security/RBAC to ensure only users with the `ADMIN` role can access or modify these endpoints and views
  - [ ] Create an initial Admin user (e.g., in `DevDataSeeder`) explicitly granted the `ADMIN` role to manage the application
  - [ ] Implement `admin-framework.html` UI for altering the framework (titles, descriptions, level details, evidence examples)
  - [ ] Implement `admin-roles.html` UI for managing roles, grades, and baseline expected pillar ratings

## 5. Polish & Verification

- [x] Implement realistic historical demo data seeding in `DevDataSeeder` (spanning a 2-year history, ensuring items are 3+ months old)
- [ ] Refine visual design (glassmorphism details, animations, responsive layout)
- [ ] Perform manual end-to-end verification
- [ ] Generate `walkthrough.md` documenting implemented flows

## GCP Deployment Task

- [x] **Target Project:** `ecdf-spring-boot-app-2026`
- [x] **Region:** `europe-west2`
- [x] **Method:** Docker-based build via Cloud Build
- [x] **Service Name:** `ecdf-mockup`
- [x] **Auth:** Allow unauthenticated (public)
- [x] **Live URL:** <https://ecdf-mockup-334478560184.europe-west2.run.app> (Deployed on 2026-03-21)
