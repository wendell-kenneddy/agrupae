package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class CourseMembership {
    private final UUID studentId;
    private final UUID courseId;
    private final Instant createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private CourseMembership(
            @NonNull final UUID studentId,
            @NonNull final UUID courseId,
            @NonNull final Instant createdAt) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.createdAt = createdAt;
    }

    public static CourseMembership create(UUID studentId, UUID courseId) {
        return CourseMembership.builder()
                .studentId(studentId)
                .courseId(courseId)
                .createdAt(Instant.now())
                .build();
    }

    public static CourseMembership reconstruct(UUID studentId, UUID courseId, Instant createdAt) {
        return CourseMembership.builder()
                .studentId(studentId)
                .courseId(courseId)
                .createdAt(createdAt)
                .build();
    }
}
