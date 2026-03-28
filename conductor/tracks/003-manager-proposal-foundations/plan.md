# TRACK: 003-manager-proposal-foundations Plan

## Phase 1: Persistence & Domain Models (TDD - Kotlin)
- [ ] Task: Create Liquibase migration for `promotion_cases` table
- [ ] Task: Define `PromotionCase` domain model and `PromotionStatus` enum
- [ ] Task: Implement repository adapter for `PromotionCase`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (TDD - Kotlin)
- [ ] Task: Implement `PromotionService.proposeCandidate`
    - [ ] Logic to ensure an open period exists and validator for direct report relationship.
- [ ] Task: Implement logic to list cases for Promotion Coordinators
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI Layer
- [ ] Task: Create Manager Propose Form (`promotion-propose.html`)
- [ ] Task: Update Coordinator Dashboard to list active cases
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Layer' (Protocol in workflow.md)
