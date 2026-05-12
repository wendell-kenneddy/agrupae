# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClass

# Build (skip tests)
./mvnw package -DskipTests

# Run Flyway migrations (requires flyway.conf)
./mvnw flyway:migrate
```

## Architecture

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for a full breakdown of the Hexagonal Architecture + DDD-Lite approach used in this project, including layer responsibilities and an end-to-end implementation walkthrough.

## Auth Flow

See [`docs/AUTH_FLOW.md`](docs/AUTH_FLOW.md) for the complete authentication reference, including endpoint contracts, token lifecycle, cookie attributes, and recommended client-side handling.

## Database

PostgreSQL (default: `localhost:6543`, DB `agrupae`, user/pass `admin`). Override with `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` env vars.

Migrations are in `src/main/resources/db/migration/` (Flyway versioned SQL). Spring Boot auto-runs pending migrations on startup. `spring.jpa.hibernate.ddl-auto=validate` — Hibernate never modifies the schema.

RSA keys default to `src/main/resources/certs/private.pem` / `public.pem`. Override with `PRIVATE_KEY` / `PUBLIC_KEY` env vars.
