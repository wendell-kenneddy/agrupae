package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CourseArtifact {
    private UUID id;
    private UUID courseId;
    private String name;
    private String description;
    private String resourceLink;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private CourseArtifact(
        @NonNull UUID id,
        @NonNull UUID courseId,
        @NonNull String name,
        String description,
        @NonNull String resourceLink,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
    ) {
        if (name.isBlank()) throw new IllegalArgumentException("Course artifact name cannot be blank.");
        if (resourceLink.isBlank()) throw new IllegalArgumentException("Resource link cannot be blank.");
        if (updatedAt.isBefore(createdAt)) throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.resourceLink = resourceLink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CourseArtifact create(
        UUID courseId,
        String name,
        String description,
        String resourceLink
    ) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        
        return CourseArtifact.builder()
                .id(id)
                .courseId(courseId)
                .name(name)
                .description(description)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
