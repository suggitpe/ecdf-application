# TRACK: 002-promotion-flow Plan

## Phase 1: Persistence & Domain Models
- [ ] Create Liquibase migration for `promotion_cases` and `promotion_feedback` tables
- [ ] Define pure Java Domain models/DTOs for Promotion entities
- [ ] Define JPA Entities and implement repository adapters

## Phase 2: Business Logic (TDD - Kotlin)
- [ ] Write Unit Tests for `PromotionService` (Initiation, feedback logic, ITA validation)
- [ ] Implement `PromotionService` (Enforcing rank-based feedback rules and manager endorsement)

## Phase 3: UI Layer (WebMvcTests - Kotlin)
- [ ] Write Controller tests and implement `PromotionController`
- [ ] Implement `promotion-proposition.html` (Case creation and aggregation)
- [ ] Implement `promotion-feedback-form.html` (Endorser statements)
- [ ] Implement `promotion-ita-review.html` (ITA evaluation dashboard)
- [ ] Implement `promotion-detail.html` (Final outcome view)

## Phase 4: Polish & Integration
- [ ] Update `DevDataSeeder` with sample promotion cases
- [ ] Link Promotion Cases to the global `Administration` or `Team` views
