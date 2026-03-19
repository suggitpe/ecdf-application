# Implementation Plan: Implement Independent Technical Assessor (ITA) Review Workflow

## Phase 1: Domain and Persistence Layer [checkpoint: 5e89aa8]
- [x] Task: Create or update Domain entity `Assessment` to support ITA workflows 6099a55
    - [x] Write Tests: Add unit tests for `Assessment` entity logic
    - [x] Implement Feature: Update `Assessment` class
- [x] Task: Update `AssessmentJpaRepository` for retrieving pending ITA reviews 2cc177b
    - [x] Write Tests: Add `@DataJpaTest` for new repository queries
    - [x] Implement Feature: Add query methods to repository
- [x] Task: Conductor - User Manual Verification 'Phase 1: Domain and Persistence Layer' (Protocol in workflow.md)

## Phase 2: Application Service Layer [checkpoint: 9eef624]
- [x] Task: Implement `AssessmentService` methods for ITA queue and scoring 9088767
    - [x] Write Tests: Add Mockito unit tests for `AssessmentService`
    - [x] Implement Feature: Add `getPendingAssessmentsForITA` and `submitITAAssessment` methods
- [x] Task: Conductor - User Manual Verification 'Phase 2: Application Service Layer' (Protocol in workflow.md)

## Phase 3: Web Layer (Controllers and Views) [checkpoint: 89ca055]
- [x] Task: Implement `AssessorController` for viewing the queue and submitting reviews 18fcc74
    - [x] Write Tests: Add `@WebMvcTest` for `AssessorController`
    - [x] Implement Feature: Create controller endpoints
- [x] Task: Create Thymeleaf view `assessor-queue.html`
    - [x] Write Tests: Verify view rendering (if applicable)
    - [x] Implement Feature: Build HTML template with Vanilla CSS
- [x] Task: Create Thymeleaf view `assessment-form.html`
    - [x] Write Tests: Verify form binding and validation
    - [x] Implement Feature: Build HTML template with blank disabled `<option>` placeholders
- [x] Task: Conductor - User Manual Verification 'Phase 3: Web Layer (Controllers and Views)' (Protocol in workflow.md)