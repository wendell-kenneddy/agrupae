package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface AcceptGroupEntryRequestUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID requestId, UUID userId);
}
