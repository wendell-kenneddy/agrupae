package com.agrupae.application.port.in.group;

import java.time.Instant;
import java.util.UUID;

public record GroupView(
    UUID id,
    UUID assignmentId,
    UUID leaderId,
    String name,
    boolean open,
    boolean membersCanEditArtifacts,
    Instant createdAt,
    Instant updatedAt
) {
}
