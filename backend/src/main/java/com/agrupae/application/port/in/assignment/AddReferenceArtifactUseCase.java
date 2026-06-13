package com.agrupae.application.port.in.assignment;

import java.util.UUID;

public interface AddReferenceArtifactUseCase {
    AssignmentArtifactView handle(UUID userId, UUID courseId, UUID assignmentId, String name, String description, String resourceLink);
}
