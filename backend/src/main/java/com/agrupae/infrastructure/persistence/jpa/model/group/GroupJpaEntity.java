package com.agrupae.infrastructure.persistence.jpa.model.group;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "groups")
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PROTECTED)
public class GroupJpaEntity {
    @Id
    private UUID id;
    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;
    @Column(name = "leader_id", nullable = false)
    private UUID leaderId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private boolean open;
    @Column(name = "members_can_edit_artifacts", nullable = false)
    private boolean membersCanEditArtifacts;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
}
