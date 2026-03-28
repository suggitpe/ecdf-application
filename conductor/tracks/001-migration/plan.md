# TRACK: 001-migration Plan

## Phase 1: Setup & Initialization
- [x] Create directory `/conductor/tracks`
- [x] Initialize track `001-migration` metadata.json, spec.md, and plan.md
- [x] Create global context files: `/conductor/context.md` and `/conductor/style.md`

## Phase 2: Migration (CLI-Ready)
- [x] Copy latest `requirements.md` to `/conductor/requirements.md`
- [x] Copy latest `AGENTS.md` to `/conductor/steering-directives.md`
- [x] Create track `002-promotion-flow` for active development
- [x] Migrate tasks for the promotion case and feedback logic from root `task.md` to track `002-promotion-flow`
- [x] Update project `README.md` to refer to the `/conductor` directory for developer context

## Phase 3: Cleanup
- [x] Decommission root-level `requirements.md`
- [x] Decommission root-level `AGENTS.md`
- [x] Decommission root-level `task.md`
