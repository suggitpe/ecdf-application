# TRACK: 007-ita-assessment-tracking Plan

## Phase 1: Persistence & Domain Models (TDD - Kotlin)
- [ ] Task: Create Liquibase migration for `ita_pillar_scores` table
- [ ] Task: Define `ItaPillarScore` and `ConcurrenceLevel` domain models
- [ ] Task: Implement repository adapter for `ItaPillarScore`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (TDD - Kotlin)
- [ ] Task: Implement `PromotionService.submitItaAssessment`
    - [ ] Save concurrence levels for all 8 pillars.
    - [ ] Save observations and recommendations.
    - [ ] Transition case to `SCORED`.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI Layer
- [ ] Task: Create ITA Assessment Form (`ita-assessment.html`)
- [ ] Task: Create Coordinator Progress Dashboard (`coordinator-tracking.html`)
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Layer' (Protocol in workflow.md)
