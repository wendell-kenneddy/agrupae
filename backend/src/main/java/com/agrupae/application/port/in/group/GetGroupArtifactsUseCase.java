package com.agrupae.application.port.in.group;

import java.util.List;
import java.util.UUID;

public interface GetGroupArtifactsUseCase {
    List<GroupArtifactView> handle(
            UUID userId,
            UUID courseId,
            UUID assignmentId,
            UUID groupId);
}
