# Specification: Implement Independent Technical Assessor (ITA) Review Workflow

## Objective
Implement the workflow that allows Third-Party Assessors (ITAs) to independently review and score submitted evidence for employees who are candidates for promotion.

## Requirements
1. **Assessor Queue**: ITAs need a dedicated dashboard/queue to see evidence submissions pending their review.
2. **Review Form**: A form where ITAs can view the employee's submitted evidence details (impact, complexity, contribution) alongside the employee's self-assessed pillar scores.
3. **Scoring**: Assessors must be able to independently score the evidence against the applicable pillars on a 1-5 scale.
4. **Feedback**: Assessors must provide a Review Summary/feedback.
5. **State Management**: Once assessed, the evidence transitions to an "assessed" state and the scores become immutable.