package com.agrupae.application.port.in.assignment;

import java.util.UUID;
import com.agrupae.domain.role.Role;

public interface DeleteAssignmentUseCase {
    public void handle(UUID actorId, Role actorRole, UUID courseId, UUID assignmentId);
}
