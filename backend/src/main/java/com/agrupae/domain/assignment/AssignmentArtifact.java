package com.agrupae.domain.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AssignmentArtifact {
    private final UUID id;
    private final UUID assignmentId;
    private String name;
    private String description;
    private String resourceLink;
    private boolean required;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private AssignmentArtifact(
            @NonNull final UUID id,
            @NonNull final UUID assignmentId,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull String resourceLink,
            final boolean required,
            @NonNull Instant createdAt,
            @NonNull Instant updatedAt) {
        if (name.isBlank())
            throw new DomainException("Assignment artifact name cannot be blank.");
        if (resourceLink.isBlank())
            throw new DomainException("Resource link cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.assignmentId = assignmentId;
        this.name = name;
        this.description = description;
        this.resourceLink = resourceLink;
        this.required = required;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AssignmentArtifact create(
            final UUID assignmentId,
            final String name,
            final String description,
            final String resourceLink) {
        return create(assignmentId, name, description, resourceLink, false);
    }

    public static AssignmentArtifact create(
            final UUID assignmentId,
            final String name,
            final String description,
            final String resourceLink,
            final boolean required) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return AssignmentArtifact.builder()
                .id(id)
                .assignmentId(assignmentId)
                .name(name)
                .description(description)
                .resourceLink(resourceLink)
                .required(required)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static AssignmentArtifact reconstruct(
            final UUID id,
            final UUID assignmentId,
            final String name,
            final String description,
            final String resourceLink,
            final Instant createdAt,
            final Instant updatedAt) {
        return reconstruct(id, assignmentId, name, description, resourceLink, false, createdAt, updatedAt);
    }

    public static AssignmentArtifact reconstruct(
            final UUID id,
            final UUID assignmentId,
            final String name,
            final String description,
            final String resourceLink,
            final boolean required,
            final Instant createdAt,
            final Instant updatedAt) {
        return AssignmentArtifact.builder()
                .id(id)
                .assignmentId(assignmentId)
                .name(name)
                .description(description)
                .resourceLink(resourceLink)
                .required(required)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void update(
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final String resourceLink) {
        update(name, description, resourceLink, false);
    }

    public void update(
            @NonNull final String name,
            @NonNull final String description,
            @NonNull final String resourceLink,
            final boolean required) {
        if (name.isBlank())
            throw new DomainException("Assignment artifact name cannot be blank.");
        if (resourceLink.isBlank())
            throw new DomainException("Resource link cannot be blank.");

        this.name = name;
        this.description = description;
        this.resourceLink = resourceLink;
        this.required = required;
        this.updatedAt = Instant.now();
    }
}