# TRACK: 001-migration

## Specification
The ECDF project is moving from a flat `requirements.md`/`task.md` model to a CLI-driven **Google Conductor** model. This track handles the initial setup of the `/conductor` directory and the migration of existing steering directives and technical rules.

## Requirements
- Create `/conductor/requirements.md` (System of Record).
- Create `/conductor/steering-directives.md` (System of Record for LLM behavior).
- Migrate current active tasks (Promotion Flow) into a dedicated Conductor track (`002-promotion-flow`).
- Decommission legacy root-level documentation files once migration is validated.
