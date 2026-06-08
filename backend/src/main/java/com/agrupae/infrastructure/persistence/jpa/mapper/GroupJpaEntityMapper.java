package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.group.Group;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupJpaEntity;

@Component
public class GroupJpaEntityMapper {

    public Group toDomain(GroupJpaEntity entity) {
        return Group.reconstruct(
            entity.getId(),
            entity.getAssignmentId(),
            entity.getLeaderId(),
            entity.getName(),
            entity.isOpen(),
            entity.isMembersCanEditArtifacts(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    public GroupJpaEntity toEntity(Group group) {
        return GroupJpaEntity.builder()
                .id(group.getId())
                .assignmentId(group.getAssignmentId())
                .leaderId(group.getLeaderId())
                .name(group.getName())
                .open(group.isOpen())
                .membersCanEditArtifacts(group.isMembersCanEditArtifacts())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .build();
    }
}
