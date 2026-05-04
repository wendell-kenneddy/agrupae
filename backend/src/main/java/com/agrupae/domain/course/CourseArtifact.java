package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CourseArtifact {
    private UUID courseId;
    private String name;
    private String description;
    private String resourceLink;
    private Instant createdAt;
    private Instant updatedAt;
}
