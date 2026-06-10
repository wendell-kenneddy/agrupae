package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface ChangeGroupModeUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId, boolean open);
}
