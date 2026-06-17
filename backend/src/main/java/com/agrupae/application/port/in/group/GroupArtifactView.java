package com.agrupae.application.port.in.group;

import java.time.Instant;
import java.util.UUID;

public record GroupArtifactView(
                UUID id,
                UUID groupId,
                String name,
                String description,
                boolean privateArtifact,
                boolean deliverable,
                Instant deliveredAt,
                String resourceLink,
                Instant createdAt,
                Instant updatedAt) {
}
