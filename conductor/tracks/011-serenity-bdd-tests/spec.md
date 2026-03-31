# TRACK: 011-serenity-bdd-tests

## Specification
This track focuses on creating a comprehensive suite of end-to-end (E2E) tests using Serenity BDD. The tests will validate the key user workflows for all major personas within the ECDF application.

## Requirements
- **Framework**: Use Serenity BDD with the Screenplay pattern.
- **Test Structure**: Employ the Page Object Model to encapsulate UI interactions.
- **Personas**: Create test suites that cover the primary workflows for the following personas:
    - **User**: Submitting evidence, viewing dashboard and check-ins.
    - **Manager**: Reviewing team, conducting check-ins, proposing candidates for promotion.
    - **ITA (Independent Technical Assessor)**: Assessing promotion cases.
    - **Admin**: Managing frameworks and user roles.
- **Reporting**: Generate web-readable HTML reports from the test execution.
- **In-App Accessibility**: The generated Serenity BDD HTML report must be integrated into the main application, making it accessible to users (likely admins) directly from within the running application. This means the report should be served as a static asset by the Spring Boot application.
