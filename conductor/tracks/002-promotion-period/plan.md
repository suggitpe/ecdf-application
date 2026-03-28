# TRACK: 002-promotion-period Plan

## Phase 1: Persistence & Domain Models (COMPLETED)
- [x] Task: Create Liquibase migration for `promotion_periods` table
- [x] Task: Define `PromotionPeriod` and `PromotionPeriodStatus` domain models
- [x] Task: Define JPA Entity and implement repository adapter for `PromotionPeriod`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (COMPLETED)
- [x] Task: Create `PromotionPeriodService` to manage periods
    - [x] Implement `openPeriod`, `closePeriod`, and `getActivePeriod` logic.
- [x] Task: Update `User` model and `SecurityConfig` for `ROLE_PROMOTION_COORDINATOR`
- [x] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI & Seeding (COMPLETED)
- [x] Task: Create Coordinator Dashboard for Promotion Periods
    - [x] `PromotionCoordinatorController` with list and create endpoints.
    - [x] `promotion-periods.html` template.
- [x] Task: Update `DevDataSeeder` with an open promotion period
- [x] Task: Conductor - User Manual Verification 'Phase 3: UI & Seeding' (Protocol in workflow.md)
