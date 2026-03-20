# Specification: Fix Thymeleaf Fragment Expressions

## Overview
Update Thymeleaf fragment expressions across all HTML templates to use the standard Thymeleaf 3 format `~{...}`. Specifically, replacing legacy `layout/base` expressions with `~{layout/base.html}` to prevent warnings and future-proof the codebase against upcoming Thymeleaf releases.

## Functional Requirements
- Identify all Thymeleaf templates using the `layout:decorate` attribute.
- Update any legacy expression (e.g., `layout/base` or `~{layout/base}`) to the standardized format `~{layout/base.html}`.

## Acceptance Criteria
- All HTML templates in `application/src/main/resources/templates/` use `~{layout/base.html}` for layout decoration.
- The application starts without Thymeleaf fragment expression warnings in the logs.
- All tests (including `@WebMvcTest` controllers) pass.
- The UI continues to render correctly with the expected layouts.

## Out of Scope
- Modifying other Thymeleaf attributes beyond `layout:decorate`.
- Changing the actual layout structure or CSS.