# TRACK: 004-sponsor-assignment-acceptance

## Specification
Enable managers to nominate three sponsors for a promotion case. Sponsors (who must be in the grade above the candidate) receive a request which they can either accept or reject.

## Requirements
- **Sponsorship Model**: Create `Sponsorship` record with `promotionCaseId`, `sponsorId`, and `status` (PENDING, ACCEPTED, REJECTED).
- **Sponsor Nomination**: Managers can nominate exactly three eligible sponsors for their proposed candidate.
- **Sponsor Workflow**: A dashboard for sponsors to view pending requests and perform the accept/reject action.
- **Status Updates**: The `PromotionCase` status updates as sponsors respond.
