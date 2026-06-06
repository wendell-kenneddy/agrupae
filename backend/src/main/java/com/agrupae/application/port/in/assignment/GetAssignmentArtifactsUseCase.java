package com.agrupae.application.port.in.assignment;

import java.util.List;
import java.util.UUID;

import lombok.NonNull;

public interface GetAssignmentArtifactsUseCase {
    List<AssignmentArtifactView> handle(
            @NonNull UUID userId,
            @NonNull UUID courseId,
            @NonNull UUID assignmentId);
}
