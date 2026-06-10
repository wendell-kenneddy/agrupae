package com.agrupae.application.port.in.group;

import java.util.UUID;

import com.agrupae.domain.role.Role;

public interface DissolveGroupUseCase {
    void handle(UUID actorId, Role actorRole, UUID courseId, UUID assignmentId, UUID groupId);
}
