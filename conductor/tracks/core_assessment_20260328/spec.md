# Track Specification: Implement Core Assessment and PDP Functionality

## Goal
Establish the core functionality of the ECDF application, enabling engineers to assess their current skill levels against a framework and automatically generate a Personal Development Plan (PDP) based on the Dreyfus model.

## User Stories
- As an Engineer, I want to view the skill framework so I can understand the expected growth path.
- As an Engineer, I want to perform a self-assessment on my skills so I can identify areas for improvement.
- As an Engineer, I want to generate a PDP from my assessment so I have a clear roadmap for my development.

## Functional Requirements
- **Framework Model**: Implement a domain model for skills, levels, and expectations.
- **Assessment Engine**: A service to capture and store skill self-assessments.
- **PDP Generator**: Logic to identify skill gaps based on the current assessment and suggest development items.
- **Persistence**: Integrate with the H2 database via Liquibase for storing assessment data.

## Non-Functional Requirements
- **Test-Driven Development**: Ensure high test coverage (>80%) with unit and integration tests.
- **Hexagonal Architecture**: Maintain a clear separation between domain logic and infrastructure.

## Technical Details
- **Modules**: Update both `:domain` and `:application` modules.
- **Testing**: Use Kotest and Mockito for unit testing; JUnit 5 for integration testing.
