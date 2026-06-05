# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com),
and this project adheres to [Semantic Versioning](https://semver.org).

## Unreleased

### Added

- (backend) Added archive method to the Assignment domain entity, with already-archived guard;
- (backend) Added POST /courses/{courseId}/assignments/{assignmentId}/archive endpoint, restricted to course leaders and admins, with exception handlers for assignment-not-found and unauthorized archival;
- (backend) Added a saveAll method to the assignment repository port and JPA adapter for batch persistence;
- (backend) Added `GET /courses/{id}` returning a single course;
- (backend) Added a view method to get info about a single course related to user. 
- (frontend) Integrated classes feature with backend endpoints (`GET /courses`, `GET /courses/{id}`, `GET /courses/{id}/members`, `POST /courses/{id}/transfer`);
- (frontend) Added archive confirmation modal and AvatarMenu component with logout;
- (frontend) Updated UI to match prototype (bottom nav, transfer ownership modal);

### Changed

- (backend) Archiving a course now also archives its active assignments in the same transaction;

### Fixed 

- (backend) Typing mistake in application.properties at "ACESS_TOKEN".
 
## [0.9.0] 2026-06-04

### Added

- (backend) Added a reconstruct factory method to the Assignment domain entity, and positive-value invariants for maxGroupMembers and maxGroups in AssignmentFlags;
- (backend) Added assignment creation application service, restricted to course leaders;
- (backend) Added JPA persistence adapters for the Assignment domain entity, covering entity, embeddable flags, repository and mappers;
- (backend) Added POST /courses/{courseId}/assignments endpoint, with service wiring and exception handlers for not-course-leader and request validation errors;
- (backend) Added unit tests for `Course` domain entity;
- (backend) Added unit tests for `transferLeadership` service;
- (backend) Added unit tests for `transferLeadership` application service;
- (backend) Added transfer leadership service and related exceptions;
- (backend) Added `POST /courses/{id}/transfer` endpoint returning the CourseView with the new leader;
- (backend) Added tests to `GetCoursesService`;
- (backend) Added a guard on `GetCoursesService` to return an empty page without querying the db when users has no memberships;
- (backend) Added a view method to get courses related to users;
- (backend) Added `GET /courses` endpoint returning a pageable JSON;
- (backend) Added archive method to the Course domain entity, with already-archived guard;
- (backend) Added course archival application service, restricted to course leaders and admins;
- (backend) Added POST /courses/{id}/archive endpoint, with wiring and exception handlers for course-not-found and unauthorized archival;
- (backend) Added new course join related exceptions into `GlobalExceptionHandler`;
- (backend) Added course join service wiring into `ApplicationServiceConfig`;
- (backend) Added course join tests;
- (backend) Added course join endpoint;
- (backend) Added course join domain and application logic;
- (backend) Added tests to course creation domain and application layers;
- (backend) Added course creation endpoint, covering domain, application and infra layers;
- (backend) Added unit tests for `User` domain entity;
- (backend) Added unit tests for `RefreshToken` domain entity;
- (backend) Added unit tests for `LoginService`, `LogoutService`, `RefreshService`, and `SignupService` application services;
- (backend) Added unit tests for `GetUserProfileService` and `UpdateProfileService` application services;
- (frontend) Added landing, login and register pages;
- (frontend) Added auth context with token management;
- (frontend) Added login and register forms with validation;
- (frontend) Added protected routes;
- (backend) Added Endpoint `PUT /users/me` to allow users to update their profile data (name and email); 
- (backend) Added strict Regex validation for email format in the User's domain entity;
- (frontend) Added getMe API call to fetch authenticated user data after login and session restore;
- (frontend) Updated auth flow to store access token in memory only, following API security contract;
- (frontend) Added automatic token refresh interceptor to axios instance;
- (frontend) Added session restore on app load via refresh token cookie;
- (frontend) Added useLogout hook to handle logout flow.

### Fixed

- (backend) Fixed tests related to Course methods.
- (backend) Fixed invalid parameters in CreateCourse calling of CourseMembership create method;
- (backend) Fixed invalid Spring placeholder default syntax for JWT TTL properties;
- (backend) Fixed user profile related services not being injected;
- (backend) Fix extra whitespace in user email blank validation error message.

### Changed

- (backend) Changed "CreateCourse" method allowing leaders to be interpreted as members of their courses in CourseMembership table;
- (backend) Changed tests related to Course methods.

## [0.8.0] 2026-05-08

### Added

- (backend) Added user profile endpoint (`GET /api/v1/users/me`) returning a `UserProfileView` DTO
  with user data excluding sensitive fields;
- (backend) Added `DomainException` handler to `GlobalExceptionHandler.java`;
- (backend) Added logout and refresh as unprotected endpoints (no authentication needed);
- (backend) Added GlobalExceptionHandler.java (self-explanatory);
- (backend) Added AuthController with it's request DTOs;
- (backend) Added a README.md file with instructions to run the backend application locally;
- (database) Added database connection properties to application.properties;
- (database) Added `V04__rename_is_open_to_open.sql` migration script to rename is_open column from
the groups table to open;
- (database) Added `V03__add_refresh_token_table.sql` migration script;
- (database) Added `v02__refactor_naming.sql` migration script to refactor database schema naming.

### Changed

- (backend) Replaced generic runtime exceptions with `DomainException` across all domain entities;
- (backend) Made `UserAlreadyExists` and `UserNotFound` exceptions extend `ApplicationException` for
  consistency with the application layer exception hierarchy;
- (backend) Moved `TokenPair.java` from the domain layer to the application layer;
- (backend) Removed userId argument from both LogoutUseCase.java and RefreshUseCase.java, since the
User entity can be derived from the RefreshToken entity already;
- (database) Moved migration files from resources/db to resources/db/migration;
- (database) Refactored migration files naming scheme from vX__Y.sql to VX__Y.sql, according to Flyways'
standards.

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

- (backend) Move auth and some user related exceptions to application layer, since they are more in line with
application concerns. Also properly refactored code related to them;
- (backend) Removed email claim from access token JWT;
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
