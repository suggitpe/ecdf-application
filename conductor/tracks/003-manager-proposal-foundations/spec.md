# TRACK: 003-manager-proposal-foundations

## Specification
Establish the core functionality for a manager to propose a candidate for promotion during an open period. Managers must provide a rationale, and Promotion Coordinators must be able to view all submitted proposals.

## Requirements
- **Promotion Case Model**: Create `PromotionCase` domain record with `candidateId`, `managerId`, `targetGradeId`, `rationale`, and `status` (PROPOSED).
- **Manager UI**: Form for managers to select a direct report and provide a written rationale.
- **Coordinator UI**: A list view showing all candidates put forward for promotion within the current period.
- **Persistence**: Store promotion cases linked to the active `PromotionPeriod`.
