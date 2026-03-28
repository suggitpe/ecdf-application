# TRACK: 004-sponsor-assignment-acceptance Plan

## Phase 1: Persistence & Domain Models (TDD - Kotlin)
- [ ] Task: Create Liquibase migration for `sponsorships` table
- [ ] Task: Define `Sponsorship` domain model and `SponsorshipStatus` enum
- [ ] Task: Implement repository adapter for `Sponsorship`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Persistence & Domain Models' (Protocol in workflow.md)

## Phase 2: Business Logic (TDD - Kotlin)
- [ ] Task: Implement `PromotionService.nominateSponsors`
    - [ ] Validation: Exactly 3 sponsors, all in the grade above the candidate.
- [ ] Task: Implement `PromotionService.respondToSponsorship` (Accept/Reject)
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Business Logic' (Protocol in workflow.md)

## Phase 3: UI Layer
- [ ] Task: Update Promotion Case detail view for managers to select sponsors
- [ ] Task: Create Sponsor Dashboard (`sponsor-inbox.html`) for responding to requests
- [ ] Task: Conductor - User Manual Verification 'Phase 3: UI Layer' (Protocol in workflow.md)
