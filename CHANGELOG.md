# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com),
and this project adheres to [Semantic Versioning](https://semver.org).

## Unreleased

### Added

- (backend) Added a README.md file with instructions to run the backend application locally.
- (database) Added database connection properties to application.properties.
- (database) Added `V04__rename_is_open_to_open.sql` migration script to rename is_open column from
the groups table to open;
- (database) Added `V03__add_refresh_token_table.sql` migration script;
- (database) Added `v02__refactor_naming.sql` migration script to refactor database schema naming.

### Changed

- (database) Moved migration files from resources/db to resources/db/migration.
- (database) Refactored migration files naming scheme from vX__Y.sql to VX__Y.sql, according to Flyways'
standards;

## [0.7.0] 2026-05-07

### Added

- (database) Initialization of database schema;
- (frontend) Scaffolded frontend application with Vite, React and TypeScript;
- (frontend) Configured Bulletproof architecture with features: auth, classes, assignments, groups;
- (frontend) Configured ESLint, Prettier and Husky with lint-staged;
- (frontend) Configured path alias @/ for clean imports;
- (backend) Added JPA adapters of User and RefreshToken domain entities, covering entities, and mappers;
- (backend) Added adapters for all authentication outbound ports;
- (backend) Added SpringSecurity setup;
- (backend) Added RSA keys and JWT config properties to application.properties;
- (backend) Added authentication domain entities and value objects;
- (backend) Added authentication use cases a.k.a inbound ports;
- (backend) Added user repository;
- (backend) Added authentication application services implementing inbound ports.

### Changed

- (backend) Refactored access level of Builder to private on GroupEntryRequests domain entity;
- (backend) Refactored indentation of fluent interface notation at every domain entity;
- (backend) Added 'throws clauses' in domain classes constructor methods;
- (backend) Fixed redundant lines of code in domain classes.

## [0.6.0] 2026-05-05SS

### Added

- Remaining group related domain entities.

### Changed

- Refactored user, courses, assignments and domain entities to expose static creation methods and
  added invariants into the constructors.

## [0.5.0] 2026-05-04

### Added

- DomainException class;
- Role domain class;
- User domain class;
- Course and CourseArtifact domain classes;
- Assignment, AssignmentFlags and ForbiddenFlagCombination domain classes;
- Group and GroupArtifact domain classes.

## [0.4.0] - 2026-05-04

### Added

- Scaffolded Java backend application.

### Changed

- Fixed backend application alias.

## [0.3.0] - 2026-04-21

### Added

- `STORIES.md`.

### Changed

- Updated `REQUIREMENTS.md` to include non functional requirements.

## [0.1.0] - 2026-04-17

### Added

- `REQUIREMENTS.md`.

## [0.0.0] - 2026-04-17

### Added

- `CHANGELOG.md`;
- Authors section to `README.md`;

### Changed

- `GUIDELINES.md` renamed to `CONTRIBUTING.md`;
- Mention to `CONTRIBUTING.md` in `README.md`.
