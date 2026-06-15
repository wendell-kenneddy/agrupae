package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface EditGroupArtifactUseCase {
    GroupArtifactView handle(
            UUID userId,
            UUID courseId,
            UUID assignmentId,
            UUID groupId,
            UUID artifactId,
            String name,
            String description,
            String resourceLink);
}
