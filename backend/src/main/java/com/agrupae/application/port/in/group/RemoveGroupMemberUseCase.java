package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface RemoveGroupMemberUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID userId, UUID memberId);
}
