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

    public static Group reconstruct(
            UUID id,
            UUID assignmentId,
            UUID leaderId,
            String name,
            boolean open,
            boolean membersCanEditArtifacts,
            Instant createdAt,
            Instant updatedAt) {
        return Group.builder()
                .id(id)
                .assignmentId(assignmentId)
                .leaderId(leaderId)
                .name(name)
                .open(open)
                .membersCanEditArtifacts(membersCanEditArtifacts)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public void editName(final String newName) {
        if (newName == null || newName.isBlank())
            throw new DomainException("Group name cannot be blank.");

        this.name = newName;
        this.updatedAt = Instant.now();
    }

    public void transferLeadership(@NonNull final UUID newLeaderId) {
        if (newLeaderId.equals(this.leaderId))
            throw new DomainException("User is already the leader of the group.");

        this.leaderId = newLeaderId;
        this.updatedAt = Instant.now();
    }

    public void toggleMode() {
        this.open = !this.open;
        this.updatedAt = Instant.now();
    }

    public void toggleMemberArtifactEdit() {
        this.membersCanEditArtifacts = !this.membersCanEditArtifacts;
        this.updatedAt = Instant.now();
    }
}
