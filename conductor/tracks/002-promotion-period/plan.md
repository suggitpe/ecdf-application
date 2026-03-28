# TRACK: 002-promotion-period Plan

## Phase 1: Persistence & Domain Models (TDD - Kotlin)
- [ ] Task: Create Liquibase migration for `promotion_periods` table
- [ ] Task: Define `PromotionPeriod` and `PromotionPeriodStatus` domain models
- [ ] Task: Define JPA Entity and implement repository adapter for `PromotionPeriod`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (TDD - Kotlin)
- [ ] Task: Create `PromotionPeriodService` to manage periods
    - [ ] Implement `openPeriod`, `closePeriod`, and `getActivePeriod` logic.
- [ ] Task: Update `User` model and `SecurityConfig` for `ROLE_PROMOTION_COORDINATOR`
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI & Seeding
- [ ] Task: Create Coordinator Dashboard for Promotion Periods
    - [ ] `PromotionCoordinatorController` with list and create endpoints.
    - [ ] `promotion-periods.html` template.
- [ ] Task: Update `DevDataSeeder` with an open promotion period
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI & Seeding' (Protocol in workflow.md)
