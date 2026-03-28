# TRACK: 003-manager-proposal-foundations Plan

## Phase 1: Persistence & Domain Models (COMPLETED)
- [x] Task: Create Liquibase migration for `promotion_cases` table
- [x] Task: Define `PromotionCase` domain model and `PromotionStatus` enum
- [x] Task: Implement repository adapter for `PromotionCase`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (COMPLETED)
- [x] Task: Implement `PromotionService.proposeCandidate`
    - [x] Logic to ensure an open period exists and validator for direct report relationship.
- [x] Task: Implement logic to list cases for Promotion Coordinators
- [x] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI Layer (COMPLETED)
- [x] Task: Create Manager Propose Form (`promotion-propose.html`)
- [x] Task: Update Coordinator Dashboard to list active cases
- [x] Task: Conductor - User Manual Verification 'Phase 3: UI Layer' (Protocol in workflow.md)
