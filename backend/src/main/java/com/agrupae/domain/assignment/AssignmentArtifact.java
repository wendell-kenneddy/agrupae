package com.agrupae.domain.assignment;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AssignmentArtifact {
    private final UUID id;
    private final UUID groupId;
    private String name;
    private String description;
    private String resourceLink;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private AssignmentArtifact(
            @NonNull final UUID id,
            @NonNull final UUID groupId,
            @NonNull final String name,
            @NonNull final String description,
            @NonNull String resourceLink,
            @NonNull Instant createdAt,
            @NonNull Instant updatedAt) throws IllegalArgumentException {
        if (name.isBlank())
            throw new IllegalArgumentException("Assignment artifact name cannot be blank.");
        if (resourceLink.isBlank())
            throw new IllegalArgumentException("Resource link cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.resourceLink = resourceLink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AssignmentArtifact create(
            final UUID groupId,
            final String name,
            final String description,
            final String resourceLink) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return AssignmentArtifact.builder()
                .id(id)
                .groupId(groupId)
                .name(name)
                .description(description)
                .resourceLink(resourceLink)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}