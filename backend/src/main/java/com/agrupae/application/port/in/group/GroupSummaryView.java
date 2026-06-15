package com.agrupae.application.port.in.group;

import java.time.Instant;
import java.util.UUID;

public record GroupSummaryView(
    UUID id,
    UUID assignmentId,
    UUID leaderId,
    String name,
    boolean open,
    boolean membersCanEditArtifacts,
    int memberCount,
    Instant createdAt,
    Instant updatedAt
) {
}
