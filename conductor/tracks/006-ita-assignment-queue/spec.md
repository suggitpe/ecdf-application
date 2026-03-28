# TRACK: 006-ita-assignment-queue

## Specification
Enable Promotion Coordinators to assign an Independent Technical Assessor (ITA) to a promotion case. Assigned cases appear in the ITA's dedicated promotion assessment queue.

## Requirements
- **ITA Assignment**: Promotion Coordinators can select an ITA (certified User) for a case in the `READY_FOR_ITA` state.
- **ITA Queue**: A new view for ITAs showing promotion cases assigned to them.
- **Promotion Status**: Assigning an ITA transitions the case to `UNDER_ITA_REVIEW`.
- **Visibility**: ITAs gain read access to the manager justification and all 3 sponsor written accounts.
