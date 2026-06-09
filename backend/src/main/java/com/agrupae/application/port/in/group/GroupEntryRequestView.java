package com.agrupae.application.port.in.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.group.GroupEntryRequestStatus;

public record GroupEntryRequestView(
    UUID id,
    UUID groupId,
    UUID userId,
    GroupEntryRequestStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
