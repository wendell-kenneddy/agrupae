package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface DeleteGroupArtifactUseCase {
    void handle(
            UUID userId,
            UUID courseId,
            UUID assignmentId,
            UUID groupId,
            UUID artifactId);
}
