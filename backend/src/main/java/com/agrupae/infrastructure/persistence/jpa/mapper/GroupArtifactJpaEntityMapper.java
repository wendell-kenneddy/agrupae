package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.group.GroupArtifact;
import com.agrupae.infrastructure.persistence.jpa.model.group.GroupArtifactJpaEntity;

@Component
public class GroupArtifactJpaEntityMapper {

    public GroupArtifact toDomain(GroupArtifactJpaEntity entity) {
        return GroupArtifact.reconstruct(
                entity.getId(),
                entity.getGroupId(),
                entity.getName(),
                entity.getDescription(),
                entity.isPrivateArtifact(),
                entity.getResourceLink(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public GroupArtifactJpaEntity toEntity(GroupArtifact artifact) {
        return GroupArtifactJpaEntity.builder()
                .id(artifact.getId())
                .groupId(artifact.getGroupId())
                .name(artifact.getName())
                .description(artifact.getDescription())
                .privateArtifact(artifact.isPrivateArtifact())
                .resourceLink(artifact.getResourceLink())
                .createdAt(artifact.getCreatedAt())
                .updatedAt(artifact.getUpdatedAt())
                .build();
    }
}
