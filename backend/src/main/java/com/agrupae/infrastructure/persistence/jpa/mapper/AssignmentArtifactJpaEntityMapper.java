package com.agrupae.infrastructure.persistence.jpa.mapper;

import org.springframework.stereotype.Component;

import com.agrupae.domain.assignment.AssignmentArtifact;
import com.agrupae.infrastructure.persistence.jpa.model.assignment.AssignmentArtifactJpaEntity;

@Component
public class AssignmentArtifactJpaEntityMapper {

    public AssignmentArtifact toDomain(AssignmentArtifactJpaEntity entity) {
        return AssignmentArtifact.reconstruct(
                entity.getId(),
                entity.getAssignmentId(),
                entity.getName(),
                entity.getDescription(),
                entity.getResourceLink(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public AssignmentArtifactJpaEntity toEntity(AssignmentArtifact artifact) {
        return AssignmentArtifactJpaEntity.builder()
                .id(artifact.getId())
                .assignmentId(artifact.getAssignmentId())
                .name(artifact.getName())
                .description(artifact.getDescription())
                .resourceLink(artifact.getResourceLink())
                .createdAt(artifact.getCreatedAt())
                .updatedAt(artifact.getUpdatedAt())
                .build();
    }
}
