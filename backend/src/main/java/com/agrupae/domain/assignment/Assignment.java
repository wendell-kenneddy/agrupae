package com.agrupae.domain.assignment;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
}
