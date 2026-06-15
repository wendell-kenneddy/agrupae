package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface AddGroupArtifactUseCase {
    GroupArtifactView handle(
            UUID userId,
            UUID courseId,
            UUID assignmentId,
            UUID groupId,
            String name,
            String description,
            boolean privateArtifact,
            String resourceLink);
}
