# TRACK: 008-promotee-force-ranking

## Specification
Implement a force ranking view for Promotion Coordinators to compare promotees based on their ITA scores. Scores are weighted: Does Not Meet = 0, Meets = 1, Exceeds = 3.

## Requirements
- **Scoring Logic**: Calculate a total "Promotion Readiness Score" for each candidate based on the 8 pillar concurrence levels.
- **Force Ranking UI**: A view for Coordinators that lists all scored candidates in the current period, ranked by their total score.
- **Detailed View**: Ability to drill down from the ranking into the full promotion case details and ITA observations.
