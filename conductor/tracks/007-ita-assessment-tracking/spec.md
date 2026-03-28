# TRACK: 007-ita-assessment-tracking

## Specification
Enable ITAs to perform pillar-by-pillar concurrence for promotion candidates. Promotion Coordinators must be able to track the overall progress of the promotion process.

## Requirements
- **ITA Assessment Model**: Create `ItaPillarScore` record mapping a `Pillar` to a `ConcurrenceLevel` (DOES_NOT_MEET, MEETS, EXCEEDS).
- **ITA UI**: A form for ITAs to score all 8 pillars for the target grade.
- **Coordinator UI**: A progress dashboard showing the current phase of each case (Proposed, Sponsoring, ITA Review, Scored).
- **Outcome**: Capturing final positive observations and development recommendations.
