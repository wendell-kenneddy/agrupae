package com.agrupae.infrastructure.persistence.jpa.model.group;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "groups_members")
@Entity
@IdClass(GroupMemberId.class)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(value = AccessLevel.PROTECTED)
public class GroupMemberJpaEntity {
    @Id
    @Column(name = "group_id", nullable = false)
    private UUID groupId;
    @Id
    @Column(name = "member_id", nullable = false)
    private UUID memberId;
    @Column
    private Instant createdAt;
}
