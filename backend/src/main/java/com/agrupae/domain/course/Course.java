package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Course {
    private final UUID id;
    private UUID leaderId;
    private String name;
    private String description;
    private String inviteCode;
    private boolean archived;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Course(
        @NonNull final UUID id,
        final UUID leaderId,
        @NonNull final String name,
        final String description,
        @NonNull final String inviteCode,
        final boolean archived,
        @NonNull final Instant createdAt,
        @NonNull final Instant updatedAt
    ) {
        if (name.isBlank()) throw new IllegalArgumentException("Course name cannot be blank.");
        if (updatedAt.isBefore(createdAt)) throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.leaderId = leaderId;
        this.name = name;
        this.description = description;
        this.inviteCode = inviteCode;
        this.archived = archived;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Course create(
        UUID leaderId,
        String name,
        String description
    ) {
        UUID id = UUID.randomUUID();
        String inviteCode = UUID.randomUUID().toString();
        Instant now = Instant.now();
        
        return Course.builder()
        .id(id)
        .leaderId(leaderId)
        .name(name)
        .description(description)
        .inviteCode(inviteCode)
        .archived(false)
        .createdAt(now)
        .updatedAt(now)
        .build();
    }
}
