package com.agrupae.domain.assignment;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class Assignment {
    private final UUID id;
    private UUID courseId;
    private String name;
    private String description;
    private AssignmentFlags assignmentFlags;
    private boolean archived;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Assignment(
            @NonNull final UUID id,
            @NonNull final UUID courseId,
            @NonNull final String name,
            final String description,
            @NonNull final AssignmentFlags assignmentFlags,
            final boolean archived,
            @NonNull final Instant dueDate,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (name.isBlank())
            throw new IllegalArgumentException("Assignment name cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");
        if (dueDate.isBefore(createdAt))
            throw new IllegalArgumentException("Due date cannot be before assignment creation timestamp.");

        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.description = description;
        this.assignmentFlags = assignmentFlags;
        this.archived = archived;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Assignment create(
            final UUID courseId,
            final String name,
            final String description,
            final Instant dueDate,
            final AssignmentFlags assignmentFlags) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return Assignment.builder()
                .id(id)
                .courseId(courseId)
                .name(name)
                .description(description)
                .assignmentFlags(assignmentFlags)
                .dueDate(dueDate)
                .archived(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
