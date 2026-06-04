package com.agrupae.application.port.in.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.assignment.AssignmentFlags;

public record AssignmentView(
    UUID id,
    UUID courseId,
    String name,
    String description,
    AssignmentFlags assignmentFlags,
    boolean isArchived,
    Instant dueDate,
    Instant createdAt,
    Instant updatedAt
) {
   
}
