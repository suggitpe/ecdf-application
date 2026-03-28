# Engineer Career Development Framework (ECDF) Application

The ECDF application is a performance and career development platform designed to help engineers track their growth against the **Engineer Career Development Framework**. It allows for evidence-based assessments, point-in-time check-ins, and automated Personal Development Plans (PDPs) based on the **Dreyfus model of skill acquisition**.

## 🚀 Quick Start

### Prerequisites

* **Java 21** (Azul Zulu recommended)
* **Docker / Podman Desktop** (for local containerized storage)
* **Google Cloud SDK** (for deployment)

### Local Development

1. **Clone the repository**
2. **Run the application locally**:

   ```bash
   ./gradlew bootRun
   ```

3. **Run the test suite**:

   ```bash
   ./gradlew test
   ```

4. **View Coverage Reports**:
   After running tests, reports are generated at `application/build/reports/jacoco/test/html/index.html`.

## 🏗️ Technical Architecture

### 🧱 Tech Stack

* **Backend**: Java 21 / Spring Boot 3.4.x
* **Build System**: Multi-module Gradle (Groovy DSL)
* **Testing**: Kotlin, JUnit 5, Mockito, Kotest, Testcontainers
* **Frontend**: Thymeleaf (SSR), Vanilla CSS (Premium Dark Mode)
* **Database**: H2 (In-memory) orchestrated via Liquibase
* **Infrastructure**: Terraform, Docker/Podman

### 🏛️ Architecture Pattern: Ports & Adapters (Hexagonal)

The project is strictly split into two main modules to ensure business logic remains isolated from infrastructure:

* **`:domain`**: Contains pure Java Domain Records/DTOs, Business Logic, and Interface "Ports" (Repositories). This module is free from Spring or DB-specific annotations.
* **`:application`**: Contains the Spring Boot infrastructure, JPA Entities, Spring MVC Controllers, and persistence "Adapters" that implement the domain ports.

## ☁️ Infrastructure & Deployment

### 🚜 Terraform

Foundation infrastructure is managed via Terraform.

1. Navigate to `/terraform`
2. Configure your `terraform.tfvars` (use `terraform.tfvars.template` as a guide)
3. Run `terraform init` and `terraform apply`.

### 📦 Containerization

The application is containerized using a native `Dockerfile` and a customized `.gcloudignore`.

* **Runtime**: Azul Zulu JRE 21 on Alpine
* **Storage**: Persistent evidence attachments are stored at `/data/storage` (mounted via GCS FUSE in production).

### 🚀 CI/CD Pipeline

A fully automated pipeline is configured in `.github/workflows/build.yml`:

* **On push to any branch**: Runs whole build, JUnit/Kotest suite, and checks JaCoCo coverage.
* **On push to `main`**: Deploys the application automatically to **Google Cloud Run** in `europe-west2`.

## 🧭 Development Model: Google Conductor

The ECDF application follows a **context-driven, CLI-first development model** using the **Google Conductor** framework.

- **System of Record**: All project requirements, technical standards, and style guides are maintained as versioned Markdown files in the `/conductor` directory.
- **Tracks**: Active development is managed through structured "Tracks" (`/conductor/tracks/`), each containing its own specification (`spec.md`) and execution plan (`plan.md`).
- **Workflow**: Planning and requirements stabilization MUST precede implementation. Progress is tracked directly in the track's `plan.md`.

## 📂 Project Structure

* `domain/`: Business logic and interfaces.
* `application/`: Infrastructure, UI, and Database adapters.
* `conductor/`: Persistent project context and active development tracks.
* `terraform/`: Infrastructure as Code scripts.
* `.github/`: CI/CD workflow definitions.

---

*Refined by Antigravity AI Engine via the Google Conductor Framework.*
