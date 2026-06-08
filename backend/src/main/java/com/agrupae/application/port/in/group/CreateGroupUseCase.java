package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface CreateGroupUseCase {
    GroupView handle(
        UUID userId,
        UUID courseId,
        UUID assignmentId,
        String name,
        boolean open
    );
}
