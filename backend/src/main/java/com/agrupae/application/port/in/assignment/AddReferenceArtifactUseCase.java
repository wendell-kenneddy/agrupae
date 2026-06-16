package com.agrupae.application.port.in.assignment;

import java.util.UUID;

import com.agrupae.domain.role.Role;

public interface AddReferenceArtifactUseCase {
    AssignmentArtifactView handle(UUID userId, Role actorRole, UUID courseId, UUID assignmentId, String name, String description, String resourceLink);
}
