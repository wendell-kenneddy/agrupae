package com.agrupae.application.port.in.assignment;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.assignment.AssignmentFlags;
import com.agrupae.domain.role.Role;

public interface EditAssignmentUseCase {
    public AssignmentView handle(
        final UUID actorId,
        final Role actorRole,
        final UUID courseId,
        final UUID assignmentId,
        final String name,
        final String description,
        final Instant dueDate,
        final AssignmentFlags assignmentFlags
    );
}
