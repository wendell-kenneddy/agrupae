package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface ChangeGroupArtifactPrivacyUseCase {
    void handle(UUID courseId, UUID assignmentId, UUID groupId, UUID artifactId, UUID userId, boolean privateArtifact);
}
