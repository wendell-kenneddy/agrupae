package com.agrupae.domain.group;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class GroupEntryRequest {
    private final UUID id;
    private UUID groupId;
    private UUID userId;
    private GroupEntryRequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private GroupEntryRequest(
            @NonNull final UUID id,
            @NonNull final UUID groupId,
            @NonNull final UUID userId,
            @NonNull final GroupEntryRequestStatus status,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Update timestamp cannot be before creation timestamp.");

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
    }

    public static GroupEntryRequest reconstruct(
            final UUID id,
            final UUID groupId,
            final UUID userId,
            final GroupEntryRequestStatus status,
            final Instant createdAt,
            final Instant updatedAt) {
        return GroupEntryRequest.builder()
                .id(id)
                .groupId(groupId)
                .userId(userId)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void accept() {
        if (this.status != GroupEntryRequestStatus.PENDING) {
            throw new DomainException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.updatedAt = now;
        this.status = GroupEntryRequestStatus.ACCEPTED;
    }

    public void reject() {
        if (this.status != GroupEntryRequestStatus.PENDING) {
            throw new DomainException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.updatedAt = now;
        this.status = GroupEntryRequestStatus.REJECTED;
    }
}
