package com.agrupae.application.port.in.group;

import java.util.UUID;

public interface ChangeGroupArtifactDeliverableStatus {
    public void handle(
            UUID courseId,
            UUID assignmentId,
            UUID groupId,
            UUID groupArtifactId,
            UUID userId,
            boolean deliverable);
}
