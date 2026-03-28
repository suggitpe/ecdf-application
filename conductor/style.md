# Coding Style & Guidelines

## 1. Technical Style
- **Java 21**: Use modern features (Records, Sealed Classes if appropriate).
- **Kotlin (Testing)**: All unit tests must be Kotlin-based.
- **TDD**: Strict Red-Green-Refactor. Write tests BEFORE implementation.
- **Assertions**: Kotest assertions.
- **Repositories**: `@DataJpaTest` with Testcontainers.
- **Controllers**: `@WebMvcTest`.

## 2. Frontend Guidelines
- **Aesthetic**: Premium Dark Mode, Glassmorphism, Modern Typography (Inter/Roboto).
- **CSS**: Vanilla CSS only (No Tailwind).
- **Icons**: FontAwesome (`fas fa-external-link-alt`).
- **Standard**:
  - Scores: Default to blank disabled `<option>`.
  - Pagination/Sort: Most recent records first.
  - Lifecycle: `DRAFT` status for iterate/editable, finalized read-only.
  - Description: Mandatory rationale for ratings.
  - Header: AcmeBank name and logo MUST be present.

## 3. Deployment
- **Docker**: Pure Dockerfile wrapper (No Jib).
- **GCP**: Target europe-west2. Cloud Run v2.
- **Terraform**: provision infrastructure.
