package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface EditGroupUseCase {
    GroupView handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId, String name);
}
