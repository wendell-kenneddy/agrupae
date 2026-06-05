package com.agrupae.application.port.in.assignment;

import java.time.Instant;
import java.util.UUID;

public record AssignmentArtifactView(
        UUID id,
        UUID assignmentId,
        String name,
        String description,
        String resourceLink,
        Instant createdAt,
        Instant updatedAt) {
}
