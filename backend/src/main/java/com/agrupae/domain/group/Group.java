package com.agrupae.domain.group;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

import com.agrupae.domain.exception.DomainException;

@Getter
public class Group {
    private final UUID id;
    private UUID assignmentId;
    private UUID leaderId;
    private String name;
    private boolean open;
    private boolean membersCanEditArtifacts;
    private Instant createdAt;
    private Instant updatedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Group(
            @NonNull final UUID id,
            @NonNull final UUID assignmentId,
            @NonNull final UUID leaderId,
            @NonNull final String name,
            final boolean open,
            final boolean membersCanEditArtifacts,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt) {
        if (name.isBlank())
            throw new DomainException("Group name cannot be blank.");
        if (updatedAt.isBefore(createdAt))
            throw new DomainException("Update timestamp cannot be before creation timestamp.");

        this.id = id;
        this.assignmentId = assignmentId;
        this.leaderId = leaderId;
        this.name = name;
        this.open = open;
        this.membersCanEditArtifacts = membersCanEditArtifacts;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Group create(
            final UUID assignmentId,
            final UUID leaderId,
            final String name,
            final boolean open,
            final boolean membersCanEditArtifacts) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();

        return Group.builder()
                .id(id)
                .assignmentId(assignmentId)
                .leaderId(leaderId)
                .name(name)
                .open(open)
                .membersCanEditArtifacts(membersCanEditArtifacts)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
