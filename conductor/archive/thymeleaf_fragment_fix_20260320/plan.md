# Implementation Plan: Fix Thymeleaf Fragment Expressions

## Phase 1: Update Templates [checkpoint: 4618093]
- [x] Task: Standardize fragment expressions in Thymeleaf templates 715f2d1
    - [ ] Write Tests: Ensure `@WebMvcTest` controllers verify view rendering for affected templates
    - [ ] Implement Feature: Update `layout:decorate` attribute in all HTML files within `application/src/main/resources/templates/` to the standard `~{layout/base.html}` format
- [x] Task: Conductor - User Manual Verification 'Phase 1: Update Templates' (Protocol in workflow.md) 4618093