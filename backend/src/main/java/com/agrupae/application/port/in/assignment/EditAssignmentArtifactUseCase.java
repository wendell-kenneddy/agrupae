package com.agrupae.application.port.in.assignment;

import java.util.UUID;
import com.agrupae.domain.role.Role;

public interface EditAssignmentArtifactUseCase {
    AssignmentArtifactView handle(
            UUID actorId,
            Role actorRole,
            UUID courseId,
            UUID assignmentId,
            UUID artifactId,
            String name,
            String description,
            String resourceLink);
}
