# Implementation Plan: Implement Independent Technical Assessor (ITA) Review Workflow

## Phase 1: Domain and Persistence Layer
- [x] Task: Create or update Domain entity `Assessment` to support ITA workflows 6099a55
    - [x] Write Tests: Add unit tests for `Assessment` entity logic
    - [x] Implement Feature: Update `Assessment` class
- [~] Task: Update `AssessmentJpaRepository` for retrieving pending ITA reviews
    - [ ] Write Tests: Add `@DataJpaTest` for new repository queries
    - [ ] Implement Feature: Add query methods to repository
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Domain and Persistence Layer' (Protocol in workflow.md)

## Phase 2: Application Service Layer
- [ ] Task: Implement `AssessmentService` methods for ITA queue and scoring
    - [ ] Write Tests: Add Mockito unit tests for `AssessmentService`
    - [ ] Implement Feature: Add `getPendingAssessmentsForITA` and `submitITAAssessment` methods
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Application Service Layer' (Protocol in workflow.md)

## Phase 3: Web Layer (Controllers and Views)
- [ ] Task: Implement `AssessorController` for viewing the queue and submitting reviews
    - [ ] Write Tests: Add `@WebMvcTest` for `AssessorController`
    - [ ] Implement Feature: Create controller endpoints
- [ ] Task: Create Thymeleaf view `assessor-queue.html`
    - [ ] Write Tests: Verify view rendering (if applicable)
    - [ ] Implement Feature: Build HTML template with Vanilla CSS
- [ ] Task: Create Thymeleaf view `assessment-form.html`
    - [ ] Write Tests: Verify form binding and validation
    - [ ] Implement Feature: Build HTML template with blank disabled `<option>` placeholders
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Web Layer (Controllers and Views)' (Protocol in workflow.md)