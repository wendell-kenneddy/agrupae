package com.agrupae.application.port.in.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.assignment.AssignmentFlags;

public interface CreateAssignmentUseCase {
    public AssignmentView handle(
        final UUID leaderId,
        final UUID courseId,
        final String name,
        final String description,
        final Instant dueDate,
        final AssignmentFlags assignmentFlags
    );
}
