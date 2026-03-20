# Engineer Career Development Framework Career Development App - Business Requirements

This document outlines the business requirements for the Engineer Career Development Framework (ECDF) application.

## 1. Domain Context: The ECDF Framework
- **ECDF**: Engineer Career Development Framework.
- **Pillars**: 8 total.
  - *Behavioral (4)*: Thinks, Engages, Influences, Achieves.
  - *Technical (4)*: Designs, Delivers, Controls, Operates.
- **Scoring**: 1 to 5 scale for each pillar, based on the **Dreyfus model of skill acquisition** (1=Novice, 2=Advanced Beginner, 3=Competent, 4=Proficient, 5=Expert).
- **Roles, Grades & Expectations**: Different roles and grade levels will have different expected baseline scores for each of the 8 pillars.
  - *Example Baseline*: A "Vice President" role defaults to an expectation of Level 3 across all pillars. A "Director" role defaults to an expectation of Level 4 across all pillars.

## 2. Evidence Submission
- Employees submit "evidence" of their work (e.g., successful project delivery, mentorship).
- Evidence must include specific details on all three points:
  1. **Impact to the firm**
  2. **Complexity to achieve**
  3. **Personal contribution**
- When submitting evidence, employees select the specific pillars they believe are applicable to the work (typically 2 to 4), NEVER all 8.
- **Iterative Creation**: The creation of evidence can happen over a period of time, allowing a manager to coach the outcomes before it is finalized.
- Once finalized and assessed, evidence is captured as an immutable historical fact.
- Employees **MUST** rate themselves on these selected pillars during submission. The dropdown inputs for these scores must default to a blank unselected state rather than a predefined score.
- **Rationale**: When a user selects a pillar rating (employee or assessor), they **MUST** also provide a short description/rationale (minimum length recommended) of why they have selected that specific rating.
- Once submitted, the manager must independently rate the evidence against those pillars.
- Evidence can include URLs to external resources or file attachments (e.g., PDF reports) as proof.

## 3. Assessments & Review
- Managers or assigned Third-Party Assessors review submitted evidence.
- Assessors score the evidence against the applicable pillars (1-5).
  - During assessment, the UI must default the assessor's scoring inputs to the exact rating submitted by the employee. If the employee did not select/rate a pillar, the default value must be a blank unselected entry.
- Assessors must provide a Review Summary/feedback for the evidence submission.

## 4. Quarterly Check-ins & Holistic Rating
- Check-ins happen on a periodic (e.g., quarterly) basis.
- **Holistic Rating & Snapshots**: During a Check-in, the system acts as a **point-in-time snapshot** of all assessed evidence. This means the ECDF assessment used for a Q1 check-in is frozen for that period and could be different from the ECDF assessment used for the Q3 check-in, as evidence evolves.
- **Evidence Aging**: Evidence has a rolling validity period (e.g., 3 years). Evidence older than this is excluded from the aggregated holistic rating.
- The manager adds formal review notes during the Check-in.
- The aggregated rating is compared against the target grade expectations.
  - The check-in UI must explicitly display the full names of the pillars, the expected pillar grades, and the actual aggregated scores.
- **Outcomes**: Based on the Check-in, the employee's status is categorized as:
  - *Ready for Promotion*: Consistently meeting/exceeding expectations for the *next* grade. **Crucially, for someone to be considered "Ready for Promotion", their evidence MUST have been independently assessed by a Third-Party who is certified as an Independent Technical Assessor (ITA).**
  - *Over Performing*: Triggered when the employee is assessing above their pillar expectations for **more than three** pillars.
  - *On Track*: Meeting expectations for the *current* grade, falling between under and over performing thresholds.
  - *Underperforming*: Triggered when the employee has **more than three** pillars not meeting the minimum threshold for their grade.

## 5. Personal Development Plan (PDP)
- Tied to a Check-in.
- When an employee is underperforming against an expected pillar score, a PDP **must** be created.
- The PDP must be connected to **learning journeys**. Actionable items should help the employee address the gap.
- For an employee who wants to progress beyond their current role, targeted pillars are specifically associated with their PDP to guide their development to the next level on those pillars.

## 6. Workflows & Permissions
- **All Users**: Default to seeing their own "Employee Record" on the Dashboard (holistic rating, target expectations, recent evidence, and a full chronological history of past check-ins).
- **Engineers**: Submit evidence, view their own assessments and Check-ins.
- **Managers**: 
  - Review direct reports' evidence and perform assessments.
  - Can view the ECDF scores for their entire reporting hierarchy (including staff reporting to managers who report to them).
  - Can compare staff grades and roles across their organization to actual ECDF pillar scores.
  - Can easily identify staff with skill gaps for their role/grade (underperforming) as well as staff with higher skills for their role/grade.
  - **Crucially**, it must be extremely easy and clear for a manager to instantly see exactly who in their organization should be considered for promotion.
  - Can assign Third-Party assessments and initiate Check-ins.
- **Third-Party Assessors**: Have a dedicated queue for evidence explicitly assigned to them by managers for independent review.

## 7. UI / UX Design
- Server-side rendered views.
- Premium Dark Mode aesthetic.
- Glassmorphism elements.
- Modern typography.
- Built-in data visualization (e.g., radar charts) to compare holistic scores against grade expectations.

## 8. Development & Sample Data
- All seeded sample data (evidence, check-ins, assessments) must be **at least three months old** relative to the date of execution to validate historical and aging logic.

## 9. Infrastructure & Deployment
- **Cloud Provider**: Google Cloud Platform (GCP).
- **Compute**: The application is deployed on **Google Cloud Run** (v2) in the `europe-west2` (London) region for serverless scalability.
- **Registry**: Container images are stored in **Google Artifact Registry**.
- **Persistence**: While the database is currently H2 in-memory, persistent evidence attachments are stored in a **Google Cloud Storage (GCS)** bucket, which is mounted to the container's `/data/storage` path using **GCS FUSE**.
- **CI/CD**: Fully automated building, testing, and delivery pipeline using **GitHub Actions**.

