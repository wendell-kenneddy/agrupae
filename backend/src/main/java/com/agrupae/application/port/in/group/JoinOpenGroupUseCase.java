package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface JoinOpenGroupUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId);
}
