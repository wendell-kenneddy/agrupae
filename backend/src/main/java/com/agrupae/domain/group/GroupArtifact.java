package com.agrupae.domain.group;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

@Getter
public class GroupArtifact {
    private final UUID id;
    private final UUID groupId;
    private String name;
    private String description;
    private String resourceLink;
    private boolean privateArtifact;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private GroupArtifact(
            @NonNull final UUID id,
            @NonNull final UUID groupId,
            @NonNull final String name,
            @NonNull final String description,
            final boolean privateArtifact,
            @NonNull String resourceLink,
            @NonNull Instant createdAt,
            @NonNull Instant updatedAt) throws IllegalArgumentException {
        if (name.isBlank())
            throw new IllegalArgumentException("Group artifact name cannot be blank.");
        if (resourceLink.isBlank())
            throw new IllegalArgumentException("Resource link cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.privateArtifact = privateArtifact;
        this.resourceLink = resourceLink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GroupArtifact create(
            final UUID groupId,
            final String name,
            final String description,
            final boolean privateArtifact,
            final String resourceLink) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return GroupArtifact.builder()
                .id(id)
                .groupId(groupId)
                .name(name)
                .description(description)
                .privateArtifact(privateArtifact)
                .resourceLink(resourceLink)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
