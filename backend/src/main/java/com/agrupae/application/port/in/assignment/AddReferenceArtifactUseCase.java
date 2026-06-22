package com.agrupae.application.port.in.assignment;

import java.util.UUID;

import com.agrupae.domain.role.Role;

public interface AddReferenceArtifactUseCase {
    AssignmentArtifactView handle(UUID userId, Role actorRole, UUID courseId, UUID assignmentId, String name, String description, String resourceLink, boolean required);

    default AssignmentArtifactView handle(UUID userId, Role actorRole, UUID courseId, UUID assignmentId, String name, String description, String resourceLink) {
        return handle(userId, actorRole, courseId, assignmentId, name, description, resourceLink, false);
    }
}
