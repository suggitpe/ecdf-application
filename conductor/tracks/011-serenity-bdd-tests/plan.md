# TRACK: 011-serenity-bdd-tests Plan

## Phase 1: Gradle Module and Serenity Setup
- [ ] Task: Create a new Gradle sub-module named `e2e-tests`.
- [ ] Task: Add the necessary Serenity BDD dependencies (`serenity-core`, `serenity-junit5`, `serenity-screenplay`, `serenity-screenplay-webdriver`) to the `e2e-tests/build.gradle` file.
- [ ] Task: Configure the `serenity-gradle-plugin` to generate aggregate reports.
- [ ] Task: Configure the Gradle build to copy the generated Serenity reports from `e2e-tests/build/site/serenity` into the main application's static resources at `application/src/main/resources/static/serenity`.
- [ ] Task: Create a simple Spring `@Controller` and a Thymeleaf view to provide a link or access point to the reports within the application (e.g., under the Admin section).

## Phase 2: Screenplay and Page Object Foundations
- [ ] Task: Establish the base package structure for the Screenplay pattern within `e2e-tests/src/test/java`:
    - `abilities`
    - `actors`
    - `interactions` (e.g., `Click`, `EnterText`)
    - `questions` (e.g., `TheText`, `IsVisible`)
    - `tasks` (e.g., `LogIn`, `NavigateTo`)
- [ ] Task: Establish the base package structure for Page Objects (or UI targets):
    - `ui` (e.g., `LoginPage`, `DashboardPage`)
- [ ] Task: Define a `CastOfActors` to provide configured actors for the tests.

## Phase 3: Test Implementation - Admin Persona
- [ ] Task: Create Page Objects for Admin login, Admin dashboard, and role management pages.
- [ ] Task: Write a `ManageRoles.feature` file in Gherkin syntax.
- [ ] Task: Implement step definitions and Screenplay tasks/questions for the Admin workflow (login, navigate to roles, verify role information).

## Phase 4: Test Implementation - User Persona
- [ ] Task: Create Page Objects for the user's dashboard, evidence submission form, and check-in list.
- [ ] Task: Write a `SubmitEvidence.feature` file.
- [ ] Task: Implement step definitions and Screenplay tasks for the User workflow (login, navigate to evidence form, submit evidence, verify submission).

## Phase 5: Test Implementation - Manager Persona
- [ ] Task: Create Page Objects for the manager's team view and check-in forms.
- [ ] Task: Write a `ConductCheckIn.feature` file.
- [ ] Task: Implement step definitions and Screenplay tasks for the Manager workflow (login, select a team member, create and finalize a check-in).

## Phase 6: Test Implementation - ITA Persona
- [ ] Task: Create Page Objects for the ITA's assessment queue and assessment form.
- [ ] Task: Write an `AssessCandidate.feature` file.
- [ ] Task: Implement step definitions and Screenplay tasks for the ITA workflow (login, select a candidate from the queue, fill out the assessment, submit).

## Phase 7: Reporting and Finalization
- [ ] Task: Execute all Serenity tests via a dedicated Gradle task (e.g., `./gradlew clean test aggregate`).
- [ ] Task: Verify that the Serenity report is generated correctly and is accessible within the running application at the `/admin/reports/serenity` (or similar) endpoint.
- [ ] Task: Update the main `conductor/tracks.md` file to officially include this new track.
- [ ] Task: Conductor - User Manual Verification 'Phase 7: Reporting and Finalization' (Protocol in workflow.md)
