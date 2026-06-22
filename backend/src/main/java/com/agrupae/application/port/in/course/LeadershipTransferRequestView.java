package com.agrupae.application.port.in.course;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.course.LeadershipTransferRequestStatus;

public record LeadershipTransferRequestView(
        UUID id,
        UUID courseId,
        UUID senderId,
        String senderName,
        UUID targetId,
        String targetName,
        LeadershipTransferRequestStatus status,
        Instant createdAt,
        Instant updatedAt) {
}
