package com.agrupae.infrastructure.persistence.jpa.mapper;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.agrupae.domain.group.GroupMember;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupMemberJpaEntity;

@Component
public class GroupMemberJpaEntityMapper {

    public GroupMember toDomain(GroupMemberJpaEntity entity) {
        return new GroupMember(entity.getGroupId(), entity.getMemberId());
    }

    public GroupMemberJpaEntity toEntity(GroupMember member) {
        return GroupMemberJpaEntity.builder()
                .groupId(member.groupId())
                .memberId(member.memberId())
                .createdAt(Instant.now())
                .build();
    }
}
