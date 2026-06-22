package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class LeadershipTransferRequest {
    private final UUID id;
    private final UUID courseId;
    private final UUID senderId;
    private final UUID targetId;
    private LeadershipTransferRequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private LeadershipTransferRequest(
            @NonNull final UUID id,
            @NonNull final UUID courseId,
            @NonNull final UUID senderId,
            @NonNull final UUID targetId,
            @NonNull final LeadershipTransferRequestStatus status,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.courseId = courseId;
        this.senderId = senderId;
        this.targetId = targetId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LeadershipTransferRequest create(
            final UUID courseId,
            final UUID senderId,
            final UUID targetId) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return LeadershipTransferRequest.builder()
                .id(id)
                .courseId(courseId)
                .senderId(senderId)
                .targetId(targetId)
                .status(LeadershipTransferRequestStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static LeadershipTransferRequest reconstruct(
            final UUID id,
            final UUID courseId,
            final UUID senderId,
            final UUID targetId,
            final LeadershipTransferRequestStatus status,
            final Instant createdAt,
            final Instant updatedAt) {
        return LeadershipTransferRequest.builder()
                .id(id)
                .courseId(courseId)
                .senderId(senderId)
                .targetId(targetId)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void accept() {
        if (this.status != LeadershipTransferRequestStatus.PENDING) {
            throw new DomainException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.status = LeadershipTransferRequestStatus.ACCEPTED;
        this.updatedAt = now;
    }

    public void reject() {
        if (this.status != LeadershipTransferRequestStatus.PENDING) {
            throw new DomainException("Only PENDING requests can be accepted/rejected.");
        }

        Instant now = Instant.now();
        this.status = LeadershipTransferRequestStatus.REJECTED;
        this.updatedAt = now;
    }
}
