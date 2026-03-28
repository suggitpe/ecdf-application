# TRACK: 005-sponsor-justification-workflow

## Specification
Enable accepted sponsors to provide their written justification for the promotion. Once all three sponsors have submitted their accounts, the case becomes ready for ITA assignment.

## Requirements
- **Justification Model**: Update `Sponsorship` record to include a `writtenAccount` and `submissionDate`.
- **Sponsor UI**: A form for accepted sponsors to write and submit their justification.
- **Progress Tracking**: Managers and Coordinators can see which sponsors have completed their justifications.
- **Readiness Trigger**: The `PromotionCase` transitions to `READY_FOR_ITA` once all 3 accepted sponsors submit their written accounts.
