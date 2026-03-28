# Project Context: ECDF Application

## 1. Domain Overview

The **Engineer Career Development Framework (ECDF)** is a performance and career growth tracking system.

- **Pillars**: 8 total (4 Behavioral: Thinks, Engages, Influences, Achieves; 4 Technical: Designs, Delivers, Controls, Operates).
- **Scoring**: 1-5 Dreyfus scale.
- **Rules**: 8 pillars only (no DEFINES). Corporate identity is **AcmeBank**.

## 2. Technical Stack

- **Backend**: Java 21, Spring Boot 3.4.x.
- **Build Tool**: Gradle (Multi-module).
- **Database**: H2 (InMemory) with Liquibase.
- **Frontend**: Thymeleaf SSR, Vanilla CSS (Glassmorphism Dark Mode).
- **Testing**: TDD with Kotlin, Kotest, JUnit 5, Mockito.

## 3. Architecture

- **Pattern**: Repository Pattern (Hexagonal / Ports & Adapters).
- **Invariants**: Domain layer must be pure Java (no framework/DB leakage).
- **Infrastructure**: GCP (Cloud Run, GCS FUSE).

## 4. Development Standards

- **Model**: CLI-driven via **Google Conductor**.
- **Process**: Planning -> Approval -> Implementation.
- **Security**: @acmebank.org corporate emails only.
