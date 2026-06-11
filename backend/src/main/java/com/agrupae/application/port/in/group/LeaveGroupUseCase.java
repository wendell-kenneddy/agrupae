package com.agrupae.application.port.in.group;

import java.util.UUID;

import lombok.NonNull;

public interface LeaveGroupUseCase {
    void handle(
            @NonNull UUID userId,
            @NonNull UUID groupId,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId);
}
