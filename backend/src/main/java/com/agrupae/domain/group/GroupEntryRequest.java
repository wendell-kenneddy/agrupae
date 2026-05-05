package com.agrupae.domain.group;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class GroupEntryRequest {
    private final UUID id;
    private UUID groupId;
    private UUID userId;
    private GroupEntryRequestStatus status = GroupEntryRequestStatus.PENDING;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @Builder
    private GroupEntryRequest(
        @NonNull final UUID id,
        @NonNull final UUID groupId,
        @NonNull final UUID userId,
        @NonNull final GroupEntryRequestStatus status,
        @NonNull final Instant createdAt,
        @NonNull final Instant updatedAt
    ) throws IllegalArgumentException {
        if (updatedAt.isBefore(createdAt)) throw new IllegalArgumentException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static GroupEntryRequest create(final UUID groupId, final UUID userId) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return GroupEntryRequest.builder()
        .id(id)
        .groupId(groupId)
        .userId(userId)
        .status(GroupEntryRequestStatus.PENDING)
        .createdAt(now)
        .updatedAt(now)
        .build();
    };

    public void accept() throws IllegalStateException {
        if (this.status != GroupEntryRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.updatedAt = now;
        this.status = GroupEntryRequestStatus.ACCEPTED;
    }

    public void reject() throws IllegalStateException {
         if (this.status != GroupEntryRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.updatedAt = now;
        this.status = GroupEntryRequestStatus.REJECTED;
    }
}
