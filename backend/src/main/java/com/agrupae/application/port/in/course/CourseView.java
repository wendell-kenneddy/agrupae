package com.agrupae.application.port.in.course;

import java.time.Instant;
import java.util.UUID;

public record CourseView(
        UUID id,
        UUID leaderId,
        String name,
        String description,
        String inviteCode,
        boolean archived,
        Instant createdAt,
        Instant updatedAt) {
}
