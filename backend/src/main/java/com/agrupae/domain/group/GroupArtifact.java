package com.agrupae.domain.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
@Setter
public class GroupArtifact {
    private UUID groupId;
    private String name;
    private String description;
    private String resourceLink;
    private boolean privateGroup;
    private Instant createdAt;
    private Instant updatedAt;
}
