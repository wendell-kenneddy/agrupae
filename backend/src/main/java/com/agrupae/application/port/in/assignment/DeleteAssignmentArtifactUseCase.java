package com.agrupae.application.port.in.assignment;

import java.util.UUID;
import com.agrupae.domain.role.Role;

public interface DeleteAssignmentArtifactUseCase {
    void handle(
            UUID actorId,
            Role actorRole,
            UUID courseId,
            UUID assignmentId,
            UUID artifactId);
}
