package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface CancelGroupEntryRequestUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID requestId, UUID userId);
}
