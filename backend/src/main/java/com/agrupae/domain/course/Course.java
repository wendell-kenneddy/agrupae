package com.agrupae.domain.course;

import java.time.Instant;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Course {
    private final UUID id;
    private UUID leaderId;
    private String name;
    private String description;
    private String inviteCode;
    private boolean archived;
    private Instant createdAt;
    private Instant updatedAt;
}
