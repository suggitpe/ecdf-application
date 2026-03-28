# TRACK: 002-promotion-period

## Specification
Implement the foundation for managing promotion time windows. This track introduces the `PromotionPeriod` domain model and the ability for a Promotion Coordinator to open and close windows during which managers can propose candidates.

## Requirements
- **Domain Model**: Create a `PromotionPeriod` record with `id`, `title`, `startDate`, `endDate`, and `status` (OPEN, CLOSED).
- **Promotion Coordinator Role**: Ensure the system recognizes a `ROLE_PROMOTION_COORDINATOR`.
- **Management UI**: A simple view for coordinators to see existing periods and open a new one.
- **Data Seeding**: Update `DevDataSeeder` to include an active (OPEN) promotion period by default.
