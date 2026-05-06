# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com),
and this project adheres to [Semantic Versioning](https://semver.org).

## Unreleased

### Changed

- (backend) Refactored access level of Builder to private on GroupEntryRequests domain entity;
- (backend) Refactored indentation of fluent interface notation at every domain entity;
- (backend) Added 'throws clauses' in domain classes constructor methods;
- (backend) Fixed redundant lines of code in domain classes.

### Added

- (frontend) Scaffolded frontend application with Vite, React and TypeScript;
- (frontend) Configured Bulletproof architecture with features: auth, classes, assignments, groups;
- (frontend) Configured ESLint, Prettier and Husky with lint-staged;
- (frontend) Configured path alias @/ for clean imports;
- (backend) Added authentication domain entities and value objects;
- (backend) Added authentication use cases a.k.a inbound ports;
- (backend) Added user repository;
- (backend) Added authentication application services implementing inbound ports.

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
