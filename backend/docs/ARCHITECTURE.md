# Architecture

This project follows **Hexagonal Architecture** (Ports & Adapters) with **DDD-Lite** practices. The goal is to keep the domain and business logic completely isolated from frameworks, databases, and delivery mechanisms.

## Core rule

Dependencies flow inward only: `infrastructure` → `application` → `domain`. The domain knows nothing about Spring, JPA, or any other framework.

```
┌────────────────────────────────────────────┐
│              infrastructure                │
│  (Spring, JPA, HTTP, Security, Config)     │
│                                            │
│   ┌────────────────────────────────────┐   │
│   │            application             │   │
│   │  (use cases, services, ports)      │   │
│   │                                    │   │
│   │   ┌────────────────────────────┐   │   │
│   │   │          domain            │   │   │
│   │   │  (entities, value objects) │   │   │
│   │   └────────────────────────────┘   │   │
│   └────────────────────────────────────┘   │
└────────────────────────────────────────────┘
```

---

## Layers

### `domain/`

Pure Java. No annotations, no framework dependencies.

Each aggregate root is a class with:
- All fields `private final` (or `private` for mutable state)
- A **private** `@Builder` (prevents arbitrary construction)
- A static `create()` factory for new instances (generates UUID, sets timestamps)
- A static `reconstruct()` factory for rehydrating from persistence (accepts existing id and timestamps)
- Invariant validation inside the builder — throws `DomainException` on violation

Value objects are Java `record`s. They validate their own invariants in the compact constructor.

Exceptions extend `DomainException` (runtime).

### `application/`

Orchestrates domain logic. No Spring annotations on services.

| Sub-package | Role |
|---|---|
| `port/in/` | Use case interfaces — the API the outside world calls |
| `port/out/` | Output port interfaces — what the application needs from infrastructure |
| `service/` | Plain classes that implement use cases; depend only on port interfaces |
| `exception/` | Typed exceptions per domain area, extending `ApplicationException` |

Response types (views) are `record`s defined alongside the use case in `port/in/`.

Services are **not** Spring `@Service` beans. They are plain classes wired manually in `infrastructure/config/ApplicationServiceConfig`. This keeps the application layer free of framework annotations.

### `infrastructure/`

Everything that touches the outside world.

| Sub-package | Role |
|---|---|
| `controller/` | Spring `@RestController`s. Request DTOs live in `controller/<domain>/dto/`. Controllers depend only on `port/in` interfaces. |
| `persistence/jpa/model/` | JPA `@Entity` classes — separate from domain entities |
| `persistence/jpa/repository/` | Spring Data `*JpaRepository` interfaces + `*RepositoryJpaAdapter` classes that implement output ports |
| `persistence/jpa/mapper/` | `@Component` mappers that convert between JPA entities and domain objects |
| `config/` | Spring bean wiring (`ApplicationServiceConfig`) |
| `config/security/` | Security filter chain, RSA key loading, JWT/token infrastructure implementations |

`GlobalExceptionHandler` (`@RestControllerAdvice`) maps domain and application exceptions to HTTP status codes.

---

## Implementation flow

The following walkthrough adds a new feature end-to-end using `Course` as the example domain.

### 1. Domain entity

```java
// domain/course/Course.java
@Getter
public class Course {
    private final UUID id;
    private UUID supervisorId;
    private String name;
    private boolean archived;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Course(
            @NonNull final UUID id,
            @NonNull final UUID supervisorId,
            @NonNull final String name,
            final boolean archived,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (name.isBlank())
            throw new DomainException("Course name cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Updated timestamp cannot be before created timestamp.");

        this.id = id;
        this.supervisorId = supervisorId;
        this.name = name;
        this.archived = archived;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Used when creating a brand new course
    public static Course create(UUID supervisorId, String name) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        return Course.builder()
                .id(id).supervisorId(supervisorId).name(name)
                .archived(false).createdAt(now).updatedAt(now).build();
    }

    // Used when loading from the database
    public static Course reconstruct(UUID id, UUID supervisorId, String name,
            boolean archived, Instant createdAt, Instant updatedAt) {
        return Course.builder()
                .id(id).supervisorId(supervisorId).name(name)
                .archived(archived).createdAt(createdAt).updatedAt(updatedAt).build();
    }
}
```

### 2. Application — output port

```java
// application/port/out/course/CourseRepository.java
public interface CourseRepository {
    Course findById(UUID id);
    Course save(Course course);
}
```

### 3. Application — use case and view

```java
// application/port/in/course/CreateCourseUseCase.java
public interface CreateCourseUseCase {
    CourseView handle(UUID supervisorId, String name);
}

// application/port/in/course/CourseView.java
public record CourseView(UUID id, String name, boolean archived, Instant createdAt) {}
```

### 4. Application — service

```java
// application/service/course/CreateCourseService.java
@RequiredArgsConstructor
public class CreateCourseService implements CreateCourseUseCase {
    private final CourseRepository courseRepository;

    @Override
    public CourseView handle(UUID supervisorId, String name) {
        Course course = Course.create(supervisorId, name);
        Course saved = courseRepository.save(course);
        return new CourseView(saved.getId(), saved.getName(), saved.isArchived(), saved.getCreatedAt());
    }
}
```

### 5. Wire the service

```java
// infrastructure/config/ApplicationServiceConfig.java
@Bean
public CreateCourseService createCourseService(CourseRepository courseRepository) {
    return new CreateCourseService(courseRepository);
}
```

### 6. Infrastructure — JPA entity

```java
// infrastructure/persistence/jpa/model/course/CourseJpaEntity.java
@Entity
@Table(name = "courses")
@Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PROTECTED)
public class CourseJpaEntity {
    @Id private UUID id;
    @Column(nullable = false) private UUID supervisorId;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private boolean archived;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant updatedAt;
}
```

### 7. Infrastructure — mapper

```java
// infrastructure/persistence/jpa/mapper/CourseJpaEntityMapper.java
@Component
public class CourseJpaEntityMapper {
    public Course toDomain(CourseJpaEntity e) {
        return Course.reconstruct(e.getId(), e.getSupervisorId(), e.getName(),
                e.isArchived(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public CourseJpaEntity toJpaEntity(Course c) {
        return CourseJpaEntity.builder()
                .id(c.getId()).supervisorId(c.getSupervisorId()).name(c.getName())
                .archived(c.isArchived()).createdAt(c.getCreatedAt()).updatedAt(c.getUpdatedAt())
                .build();
    }
}
```

### 8. Infrastructure — JPA repository + adapter

```java
// infrastructure/persistence/jpa/repository/course/CourseJpaRepository.java
public interface CourseJpaRepository extends JpaRepository<CourseJpaEntity, UUID> {}

// infrastructure/persistence/jpa/repository/course/CourseRepositoryJpaAdapter.java
@Repository
@RequiredArgsConstructor
public class CourseRepositoryJpaAdapter implements CourseRepository {
    private final CourseJpaRepository jpaRepository;
    private final CourseJpaEntityMapper mapper;

    @Override
    public Course findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain).orElse(null);
    }

    @Override
    public Course save(Course course) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(course)));
    }
}
```

### 9. Infrastructure — controller

```java
// infrastructure/controller/course/CourseController.java
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CreateCourseUseCase createCourseUseCase;

    @PostMapping
    public ResponseEntity<CourseView> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateCourseRequest request) {
        UUID supervisorId = UUID.fromString(jwt.getSubject());
        CourseView view = createCourseUseCase.handle(supervisorId, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }
}
```

### 10. Exception handling

- Throw `DomainException` (or a subclass) from domain entities for invariant violations — mapped to `422 Unprocessable Content`.
- Throw typed subclasses of `ApplicationException` from services for business-rule failures (e.g. `UserNotFoundException`) — mapped in `GlobalExceptionHandler` to the appropriate HTTP status.
- Add new exception mappings to `GlobalExceptionHandler` when introducing new exception types.
